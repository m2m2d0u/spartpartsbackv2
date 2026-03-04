package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sn.symmetry.spareparts.dto.request.BulkImportPartRequest;
import sn.symmetry.spareparts.dto.response.BulkImportResultResponse;
import sn.symmetry.spareparts.dto.response.PartImportErrorResponse;
import sn.symmetry.spareparts.dto.response.PartImportWarningResponse;
import sn.symmetry.spareparts.dto.response.PartResponse;
import sn.symmetry.spareparts.entity.CarBrand;
import sn.symmetry.spareparts.entity.CarModel;
import sn.symmetry.spareparts.entity.Category;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.Tag;
import sn.symmetry.spareparts.mapper.PartMapper;
import sn.symmetry.spareparts.repository.CarBrandRepository;
import sn.symmetry.spareparts.repository.CarModelRepository;
import sn.symmetry.spareparts.repository.CategoryRepository;
import sn.symmetry.spareparts.repository.PartRepository;
import sn.symmetry.spareparts.repository.TagRepository;
import sn.symmetry.spareparts.service.PartImportService;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartImportServiceImpl implements PartImportService {

    private final PartRepository partRepository;
    private final CategoryRepository categoryRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;
    private final TagRepository tagRepository;
    private final PartMapper partMapper;

    @Override
    @Transactional
    public BulkImportResultResponse importParts(MultipartFile file) {
        long startTime = System.currentTimeMillis();

        List<BulkImportPartRequest> requests;
        try {
            requests = parseFile(file);
        } catch (IOException e) {
            log.error("Failed to parse file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse file: " + e.getMessage());
        }

        List<PartImportErrorResponse> errors = new ArrayList<>();
        List<PartImportWarningResponse> warnings = new ArrayList<>();
        List<PartResponse> importedParts = new ArrayList<>();

        for (BulkImportPartRequest request : requests) {
            // Check for duplicates
            if (partRepository.existsByPartNumber(request.getPartNumber())) {
                warnings.add(PartImportWarningResponse.builder()
                        .rowNumber(request.getRowNumber())
                        .partNumber(request.getPartNumber())
                        .warningMessage("Part with number '" + request.getPartNumber() + "' already exists, skipping row " + request.getRowNumber())
                        .build());
                continue;
            }

            // Validate and convert
            PartImportErrorResponse error = validateAndConvertRow(request);
            if (!error.getErrors().isEmpty()) {
                errors.add(error);
                continue;
            }

            // Convert to Part entity and save
            try {
                Part part = convertToPart(request);
                Part savedPart = partRepository.save(part);
                importedParts.add(partMapper.toResponse(savedPart));
            } catch (Exception e) {
                log.error("Failed to save part {}: {}", request.getPartNumber(), e.getMessage(), e);
                PartImportErrorResponse saveError = PartImportErrorResponse.builder()
                        .rowNumber(request.getRowNumber())
                        .partNumber(request.getPartNumber())
                        .errors(List.of(PartImportErrorResponse.FieldError.builder()
                                .field("general")
                                .errorMessage("Failed to save part: " + e.getMessage())
                                .rejectedValue(null)
                                .build()))
                        .build();
                errors.add(saveError);
            }
        }

        long endTime = System.currentTimeMillis();

        return BulkImportResultResponse.builder()
                .totalRows(requests.size())
                .successCount(importedParts.size())
                .failureCount(errors.size())
                .duplicateCount(warnings.size())
                .processingTimeMs(endTime - startTime)
                .errors(errors)
                .warnings(warnings)
                .importedParts(importedParts)
                .build();
    }

    private List<BulkImportPartRequest> parseFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("File name is missing");
        }

        if (filename.toLowerCase().endsWith(".csv")) {
            return parseCsvFile(file);
        } else if (filename.toLowerCase().endsWith(".xlsx")) {
            return parseExcelFile(file);
        } else {
            throw new IllegalArgumentException("Unsupported file format. Only CSV and XLSX are supported.");
        }
    }

    private List<BulkImportPartRequest> parseCsvFile(MultipartFile file) throws IOException {
        List<BulkImportPartRequest> requests = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build())) {

            int rowNumber = 1; // Start from 1 (header is row 0)
            for (CSVRecord record : csvParser) {
                rowNumber++;
                BulkImportPartRequest request = new BulkImportPartRequest();
                request.setRowNumber(rowNumber);
                request.setPartNumber(getStringValue(record, "partNumber"));
                request.setName(getStringValue(record, "name"));
                request.setDescription(getStringValue(record, "description"));
                request.setShortDescription(getStringValue(record, "shortDescription"));
                request.setCategoryName(getStringValue(record, "categoryName"));
                request.setCarBrandName(getStringValue(record, "carBrandName"));
                request.setCarModelName(getStringValue(record, "carModelName"));
                request.setSellingPrice(getBigDecimalValue(record, "sellingPrice"));
                request.setPurchasePrice(getBigDecimalValue(record, "purchasePrice"));
                request.setMinStockLevel(getIntegerValue(record, "minStockLevel"));
                request.setPublished(getBooleanValue(record, "published"));
                request.setNotes(getStringValue(record, "notes"));
                request.setTags(getStringValue(record, "tags"));

                requests.add(request);
            }
        }

        return requests;
    }

    private List<BulkImportPartRequest> parseExcelFile(MultipartFile file) throws IOException {
        List<BulkImportPartRequest> requests = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Read header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel file is missing header row");
            }

            Map<String, Integer> columnMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String columnName = cell.getStringCellValue().trim();
                columnMap.put(columnName.toLowerCase(), cell.getColumnIndex());
            }

            // Read data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                BulkImportPartRequest request = new BulkImportPartRequest();
                request.setRowNumber(i + 1);
                request.setPartNumber(getCellStringValue(row, columnMap, "partnumber"));
                request.setName(getCellStringValue(row, columnMap, "name"));
                request.setDescription(getCellStringValue(row, columnMap, "description"));
                request.setShortDescription(getCellStringValue(row, columnMap, "shortdescription"));
                request.setCategoryName(getCellStringValue(row, columnMap, "categoryname"));
                request.setCarBrandName(getCellStringValue(row, columnMap, "carbrandname"));
                request.setCarModelName(getCellStringValue(row, columnMap, "carmodelname"));
                request.setSellingPrice(getCellBigDecimalValue(row, columnMap, "sellingprice"));
                request.setPurchasePrice(getCellBigDecimalValue(row, columnMap, "purchaseprice"));
                request.setMinStockLevel(getCellIntegerValue(row, columnMap, "minstocklevel"));
                request.setPublished(getCellBooleanValue(row, columnMap, "published"));
                request.setNotes(getCellStringValue(row, columnMap, "notes"));
                request.setTags(getCellStringValue(row, columnMap, "tags"));

                requests.add(request);
            }
        }

        return requests;
    }

    private PartImportErrorResponse validateAndConvertRow(BulkImportPartRequest request) {
        List<PartImportErrorResponse.FieldError> fieldErrors = new ArrayList<>();

        // Validate required fields
        if (request.getPartNumber() == null || request.getPartNumber().trim().isEmpty()) {
            fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                    .field("partNumber")
                    .errorMessage("Part number is required")
                    .rejectedValue(request.getPartNumber())
                    .build());
        } else if (request.getPartNumber().length() > 50) {
            fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                    .field("partNumber")
                    .errorMessage("Part number must not exceed 50 characters")
                    .rejectedValue(request.getPartNumber())
                    .build());
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                    .field("name")
                    .errorMessage("Name is required")
                    .rejectedValue(request.getName())
                    .build());
        } else if (request.getName().length() > 200) {
            fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                    .field("name")
                    .errorMessage("Name must not exceed 200 characters")
                    .rejectedValue(request.getName())
                    .build());
        }

        if (request.getSellingPrice() == null) {
            fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                    .field("sellingPrice")
                    .errorMessage("Selling price is required")
                    .rejectedValue(null)
                    .build());
        } else if (request.getSellingPrice().compareTo(BigDecimal.ZERO) < 0) {
            fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                    .field("sellingPrice")
                    .errorMessage("Selling price must be at least 0")
                    .rejectedValue(request.getSellingPrice())
                    .build());
        }

        if (request.getPurchasePrice() == null) {
            fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                    .field("purchasePrice")
                    .errorMessage("Purchase price is required")
                    .rejectedValue(null)
                    .build());
        } else if (request.getPurchasePrice().compareTo(BigDecimal.ZERO) < 0) {
            fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                    .field("purchasePrice")
                    .errorMessage("Purchase price must be at least 0")
                    .rejectedValue(request.getPurchasePrice())
                    .build());
        }

        // Validate optional fields
        if (request.getShortDescription() != null && request.getShortDescription().length() > 500) {
            fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                    .field("shortDescription")
                    .errorMessage("Short description must not exceed 500 characters")
                    .rejectedValue(request.getShortDescription())
                    .build());
        }

        if (request.getMinStockLevel() != null && request.getMinStockLevel() < 0) {
            fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                    .field("minStockLevel")
                    .errorMessage("Minimum stock level must be at least 0")
                    .rejectedValue(request.getMinStockLevel())
                    .build());
        }

        // Validate foreign keys
        if (request.getCategoryName() != null && !request.getCategoryName().trim().isEmpty()) {
            if (categoryRepository.findByNameIgnoreCase(request.getCategoryName()).isEmpty()) {
                fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                        .field("categoryName")
                        .errorMessage("Category with name '" + request.getCategoryName() + "' not found")
                        .rejectedValue(request.getCategoryName())
                        .build());
            }
        }

        if (request.getCarBrandName() != null && !request.getCarBrandName().trim().isEmpty()) {
            if (carBrandRepository.findByNameIgnoreCase(request.getCarBrandName()).isEmpty()) {
                fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                        .field("carBrandName")
                        .errorMessage("Car brand with name '" + request.getCarBrandName() + "' not found")
                        .rejectedValue(request.getCarBrandName())
                        .build());
            }
        }

        if (request.getCarModelName() != null && !request.getCarModelName().trim().isEmpty()) {
            if (request.getCarBrandName() == null || request.getCarBrandName().trim().isEmpty()) {
                fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                        .field("carModelName")
                        .errorMessage("Car brand name is required when car model name is provided")
                        .rejectedValue(request.getCarModelName())
                        .build());
            } else {
                CarBrand brand = carBrandRepository.findByNameIgnoreCase(request.getCarBrandName()).orElse(null);
                if (brand != null) {
                    if (carModelRepository.findByNameIgnoreCaseAndBrand(request.getCarModelName(), brand).isEmpty()) {
                        fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                                .field("carModelName")
                                .errorMessage("Car model with name '" + request.getCarModelName() + "' not found for brand '" + request.getCarBrandName() + "'")
                                .rejectedValue(request.getCarModelName())
                                .build());
                    }
                }
            }
        }

        // Validate tags
        if (request.getTags() != null && !request.getTags().trim().isEmpty()) {
            String[] tagNames = request.getTags().split(",");
            for (String tagName : tagNames) {
                String trimmedTagName = tagName.trim();
                if (!trimmedTagName.isEmpty() && tagRepository.findByNameIgnoreCase(trimmedTagName).isEmpty()) {
                    fieldErrors.add(PartImportErrorResponse.FieldError.builder()
                            .field("tags")
                            .errorMessage("Tag with name '" + trimmedTagName + "' not found")
                            .rejectedValue(trimmedTagName)
                            .build());
                }
            }
        }

        return PartImportErrorResponse.builder()
                .rowNumber(request.getRowNumber())
                .partNumber(request.getPartNumber())
                .errors(fieldErrors)
                .build();
    }

    private Part convertToPart(BulkImportPartRequest request) {
        Part part = new Part();
        part.setPartNumber(request.getPartNumber());
        part.setName(request.getName());
        part.setDescription(request.getDescription());
        part.setShortDescription(request.getShortDescription());
        part.setSellingPrice(request.getSellingPrice());
        part.setPurchasePrice(request.getPurchasePrice());
        part.setMinStockLevel(request.getMinStockLevel() != null ? request.getMinStockLevel() : 0);
        part.setPublished(request.getPublished() != null ? request.getPublished() : false);
        part.setNotes(request.getNotes());

        // Resolve category
        if (request.getCategoryName() != null && !request.getCategoryName().trim().isEmpty()) {
            Category category = categoryRepository.findByNameIgnoreCase(request.getCategoryName()).orElse(null);
            part.setCategory(category);
        }

        // Resolve car brand
        if (request.getCarBrandName() != null && !request.getCarBrandName().trim().isEmpty()) {
            CarBrand carBrand = carBrandRepository.findByNameIgnoreCase(request.getCarBrandName()).orElse(null);
            part.setCarBrand(carBrand);

            // Resolve car model
            if (request.getCarModelName() != null && !request.getCarModelName().trim().isEmpty() && carBrand != null) {
                CarModel carModel = carModelRepository.findByNameIgnoreCaseAndBrand(request.getCarModelName(), carBrand).orElse(null);
                part.setCarModel(carModel);
            }
        }

        // Resolve tags
        if (request.getTags() != null && !request.getTags().trim().isEmpty()) {
            String[] tagNames = request.getTags().split(",");
            List<Tag> tags = new ArrayList<>();
            for (String tagName : tagNames) {
                String trimmedTagName = tagName.trim();
                if (!trimmedTagName.isEmpty()) {
                    tagRepository.findByNameIgnoreCase(trimmedTagName).ifPresent(tags::add);
                }
            }
            part.setTags(tags);
        }

        return part;
    }

    // CSV helper methods
    private String getStringValue(CSVRecord record, String columnName) {
        try {
            String value = record.get(columnName);
            return (value == null || value.trim().isEmpty()) ? null : value.trim();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private BigDecimal getBigDecimalValue(CSVRecord record, String columnName) {
        try {
            String value = getStringValue(record, columnName);
            return (value == null) ? null : new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getIntegerValue(CSVRecord record, String columnName) {
        try {
            String value = getStringValue(record, columnName);
            return (value == null) ? null : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean getBooleanValue(CSVRecord record, String columnName) {
        try {
            String value = getStringValue(record, columnName);
            if (value == null) return null;
            return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
        } catch (Exception e) {
            return null;
        }
    }

    // Excel helper methods
    private String getCellStringValue(Row row, Map<String, Integer> columnMap, String columnName) {
        Integer colIndex = columnMap.get(columnName.toLowerCase());
        if (colIndex == null) return null;

        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;

        if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue();
            return (value == null || value.trim().isEmpty()) ? null : value.trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return null;
    }

    private BigDecimal getCellBigDecimalValue(Row row, Map<String, Integer> columnMap, String columnName) {
        Integer colIndex = columnMap.get(columnName.toLowerCase());
        if (colIndex == null) return null;

        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : new BigDecimal(value);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private Integer getCellIntegerValue(Row row, Map<String, Integer> columnMap, String columnName) {
        Integer colIndex = columnMap.get(columnName.toLowerCase());
        if (colIndex == null) return null;

        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : Integer.parseInt(value);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private Boolean getCellBooleanValue(Row row, Map<String, Integer> columnMap, String columnName) {
        Integer colIndex = columnMap.get(columnName.toLowerCase());
        if (colIndex == null) return null;

        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.BOOLEAN) {
                return cell.getBooleanCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
            } else if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue() != 0;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public byte[] generateImportTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Parts Import Template");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "partNumber", "name", "sellingPrice", "purchasePrice",
                    "description", "shortDescription", "categoryName",
                    "carBrandName", "carModelName", "minStockLevel",
                    "published", "notes", "tags"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000);
            }

            // Add sample data rows
            addSampleRow(sheet, 1, "BRK-001", "Front Brake Pads", "89.99", "45.50",
                    "High-quality ceramic brake pads", "Ceramic brake pads for front axle",
                    "Brakes", "Toyota", "Camry", "10", "true", "Premium quality", "Premium,Popular");

            addSampleRow(sheet, 2, "FLT-002", "Oil Filter", "12.99", "6.25",
                    "Standard oil filter for engine", "Engine oil filter",
                    "Filters", "Honda", "Accord", "20", "true", "OEM replacement", "Standard,OEM");

            addSampleRow(sheet, 3, "SPK-003", "Spark Plugs Set", "45.00", "22.50",
                    "Set of 4 iridium spark plugs", "Iridium spark plugs",
                    "Ignition", "Ford", "F-150", "5", "false", "High performance", "Performance");

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void addSampleRow(Sheet sheet, int rowNum, String partNumber, String name,
                              String sellingPrice, String purchasePrice, String description,
                              String shortDescription, String categoryName, String carBrandName,
                              String carModelName, String minStockLevel, String published,
                              String notes, String tags) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(partNumber);
        row.createCell(1).setCellValue(name);
        row.createCell(2).setCellValue(sellingPrice);
        row.createCell(3).setCellValue(purchasePrice);
        row.createCell(4).setCellValue(description);
        row.createCell(5).setCellValue(shortDescription);
        row.createCell(6).setCellValue(categoryName);
        row.createCell(7).setCellValue(carBrandName);
        row.createCell(8).setCellValue(carModelName);
        row.createCell(9).setCellValue(minStockLevel);
        row.createCell(10).setCellValue(published);
        row.createCell(11).setCellValue(notes);
        row.createCell(12).setCellValue(tags);
    }
}
