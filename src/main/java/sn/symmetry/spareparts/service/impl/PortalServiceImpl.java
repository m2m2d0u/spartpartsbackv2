package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.config.CacheConfig;
import sn.symmetry.spareparts.dto.request.portal.PortalCreateOrderRequest;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalCarBrandResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalCarModelResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalCategoryResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalOrderConfirmationResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalPartDetailResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalPartResponse;
import sn.symmetry.spareparts.dto.response.portal.PortalStoreConfigResponse;
import sn.symmetry.spareparts.entity.CarBrand;
import sn.symmetry.spareparts.entity.CarModel;
import sn.symmetry.spareparts.entity.ClientOrder;
import sn.symmetry.spareparts.entity.Customer;
import sn.symmetry.spareparts.entity.OrderItem;
import sn.symmetry.spareparts.entity.Part;
import sn.symmetry.spareparts.entity.PartImage;
import sn.symmetry.spareparts.entity.Store;
import sn.symmetry.spareparts.enums.OrderStatus;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.repository.CarBrandRepository;
import sn.symmetry.spareparts.repository.CarModelRepository;
import sn.symmetry.spareparts.repository.CategoryRepository;
import sn.symmetry.spareparts.repository.ClientOrderRepository;
import sn.symmetry.spareparts.repository.CustomerRepository;
import sn.symmetry.spareparts.repository.PartRepository;
import sn.symmetry.spareparts.repository.StoreRepository;
import sn.symmetry.spareparts.repository.WarehouseStockRepository;
import sn.symmetry.spareparts.service.FileStorageService;
import sn.symmetry.spareparts.service.PortalService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortalServiceImpl implements PortalService {

    private final PartRepository partRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final CategoryRepository categoryRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;
    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final FileStorageService fileStorageService;

    @Override
    public PagedResponse<PortalPartResponse> searchParts(String name, UUID categoryId, UUID carBrandId, UUID carModelId, Pageable pageable) {
        // Always filter by published = true and only show parts with stock > 0
        Page<Part> page = partRepository.searchPartsWithStock(name, categoryId, true, carBrandId, carModelId, pageable);

        // Batch load total stock for all parts in the page
        List<UUID> partIds = page.getContent().stream().map(Part::getId).toList();
        Map<UUID, Integer> stockMap = getStockMap(partIds);

        Page<PortalPartResponse> mapped = page.map(part -> toPortalPartResponse(part, stockMap.getOrDefault(part.getId(), 0)));
        return PagedResponse.of(mapped);
    }

    @Override
    @Cacheable(value = CacheConfig.PORTAL_PARTS_CACHE, key = "#id.toString()")
    public PortalPartDetailResponse getPartById(UUID id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", id));

        if (!Boolean.TRUE.equals(part.getPublished())) {
            throw new ResourceNotFoundException("Part", "id", id);
        }

        int totalStock = warehouseStockRepository.getTotalStockByPartId(id);

        return toPortalPartDetailResponse(part, totalStock);
    }

    @Override
    @Cacheable(CacheConfig.PORTAL_CATEGORIES_CACHE)
    public List<PortalCategoryResponse> getCategories() {
        List<Object[]> results = categoryRepository.findCategoriesWithInStockPartCount();
        return results.stream()
                .map(row -> PortalCategoryResponse.builder()
                        .id(row[0].toString())
                        .name((String) row[1])
                        .imageUrl((String) row[2])
                        .partCount((Long) row[3])
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(CacheConfig.PORTAL_CAR_BRANDS_CACHE)
    public List<PortalCarBrandResponse> getCarBrands() {
        List<CarBrand> brands = carBrandRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        return brands.stream()
                .map(b -> PortalCarBrandResponse.builder()
                        .id(b.getId().toString())
                        .name(b.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CacheConfig.PORTAL_CAR_MODELS_CACHE, key = "#brandId != null ? #brandId.toString() : 'all'")
    public List<PortalCarModelResponse> getCarModels(UUID brandId) {
        List<CarModel> models;
        if (brandId != null) {
            models = carModelRepository.findByBrandId(brandId);
        } else {
            models = carModelRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        }
        return models.stream()
                .map(m -> PortalCarModelResponse.builder()
                        .id(m.getId().toString())
                        .name(m.getName())
                        .brandId(m.getBrand().getId().toString())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(CacheConfig.PORTAL_STORE_CONFIG_CACHE)
    public PortalStoreConfigResponse getStoreConfig() {
        Store store = storeRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Store", "isActive", true));

        return PortalStoreConfigResponse.builder()
                .storeName(store.getName())
                .logoUrl(store.getLogoUrl())
                .currencySymbol(store.getCurrencySymbol() != null ? store.getCurrencySymbol() : "$")
                .currencyPosition(store.getCurrencyPosition() != null ? store.getCurrencyPosition() : "before")
                .currencyDecimals(store.getCurrencyDecimals() != null ? store.getCurrencyDecimals() : 2)
                .thousandsSeparator(store.getThousandsSeparator() != null ? store.getThousandsSeparator() : ",")
                .build();
    }

    @Override
    @Transactional
    public PortalOrderConfirmationResponse createOrder(PortalCreateOrderRequest request) {
        // Find or create customer by email
        Customer customer = customerRepository.findByEmail(request.getCustomer().getEmail())
                .orElseGet(() -> createCustomerFromRequest(request.getCustomer()));

        // Build order
        ClientOrder order = new ClientOrder();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setNotes(request.getNotes());
        order.setOrderDate(LocalDateTime.now());

        // Use customer address as shipping address
        order.setShippingStreet(request.getCustomer().getStreet());
        order.setShippingCity(request.getCustomer().getCity());
        order.setShippingState(request.getCustomer().getState());
        order.setShippingPostal(request.getCustomer().getPostalCode());
        order.setShippingCountry(request.getCustomer().getCountry());

        // Build items using the part's selling price
        List<OrderItem> items = new ArrayList<>();
        for (PortalCreateOrderRequest.PortalOrderItemRequest itemReq : request.getItems()) {
            Part part = partRepository.findById(itemReq.getPartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Part", "id", itemReq.getPartId()));

            OrderItem item = new OrderItem();
            item.setClientOrder(order);
            item.setPart(part);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(part.getSellingPrice());
            item.setTotalPrice(part.getSellingPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            items.add(item);
        }
        order.setItems(items);

        BigDecimal subtotal = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubtotal(subtotal);
        order.setTotalAmount(subtotal);

        ClientOrder saved = clientOrderRepository.save(order);

        return toOrderConfirmation(saved);
    }

    @Override
    public PortalOrderConfirmationResponse getOrderByNumber(String orderNumber) {
        ClientOrder order = clientOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        return toOrderConfirmation(order);
    }

    // --- Private helper methods ---

    private Map<UUID, Integer> getStockMap(List<UUID> partIds) {
        if (partIds.isEmpty()) {
            return Map.of();
        }
        List<Object[]> results = warehouseStockRepository.getTotalStockByPartIds(partIds);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> ((Number) row[1]).intValue()
                ));
    }

    private PortalPartResponse toPortalPartResponse(Part part, int availableStock) {
        String mainImageUrl = null;
        if (part.getImages() != null && !part.getImages().isEmpty()) {
            PartImage mainImage = part.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsMain()))
                    .findFirst()
                    .orElse(part.getImages().getFirst());
            if (mainImage.getReference() != null) {
                mainImageUrl = fileStorageService.getPublicUrl(mainImage.getReference());
            }
        }

        return PortalPartResponse.builder()
                .id(part.getId().toString())
                .partNumber(part.getPartNumber())
                .name(part.getName())
                .shortDescription(part.getShortDescription())
                .sellingPrice(part.getSellingPrice())
                .categoryName(part.getCategory() != null ? part.getCategory().getName() : null)
                .carBrandName(part.getCarBrand() != null ? part.getCarBrand().getName() : null)
                .carModelName(part.getCarModel() != null ? part.getCarModel().getName() : null)
                .mainImageUrl(mainImageUrl)
                .availableStock(availableStock)
                .build();
    }

    private PortalPartDetailResponse toPortalPartDetailResponse(Part part, int availableStock) {
        List<PortalPartDetailResponse.PortalImageResponse> images = List.of();
        if (part.getImages() != null) {
            images = part.getImages().stream()
                    .map(img -> PortalPartDetailResponse.PortalImageResponse.builder()
                            .id(img.getId().toString())
                            .url(img.getReference() != null ? fileStorageService.getPublicUrl(img.getReference()) : null)
                            .sortOrder(img.getSortOrder())
                            .isMain(img.getIsMain())
                            .build())
                    .toList();
        }

        List<PortalPartDetailResponse.PortalTagResponse> tags = List.of();
        if (part.getTags() != null) {
            tags = part.getTags().stream()
                    .map(tag -> PortalPartDetailResponse.PortalTagResponse.builder()
                            .id(tag.getId().toString())
                            .name(tag.getName())
                            .build())
                    .toList();
        }

        return PortalPartDetailResponse.builder()
                .id(part.getId().toString())
                .partNumber(part.getPartNumber())
                .name(part.getName())
                .shortDescription(part.getShortDescription())
                .description(part.getDescription())
                .sellingPrice(part.getSellingPrice())
                .categoryName(part.getCategory() != null ? part.getCategory().getName() : null)
                .carBrandName(part.getCarBrand() != null ? part.getCarBrand().getName() : null)
                .carModelName(part.getCarModel() != null ? part.getCarModel().getName() : null)
                .images(images)
                .tags(tags)
                .availableStock(availableStock)
                .build();
    }

    private Customer createCustomerFromRequest(PortalCreateOrderRequest.PortalCustomerInfo info) {
        Customer customer = new Customer();
        customer.setName(info.getName());
        customer.setEmail(info.getEmail());
        customer.setPhone(info.getPhone());
        customer.setStreet(info.getStreet());
        customer.setCity(info.getCity());
        customer.setState(info.getState());
        customer.setPostalCode(info.getPostalCode());
        customer.setCountry(info.getCountry());
        return customerRepository.save(customer);
    }

    private PortalOrderConfirmationResponse toOrderConfirmation(ClientOrder order) {
        List<PortalOrderConfirmationResponse.PortalOrderItemResponse> items = order.getItems().stream()
                .map(item -> PortalOrderConfirmationResponse.PortalOrderItemResponse.builder()
                        .partName(item.getPart().getName())
                        .partNumber(item.getPart().getPartNumber())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();

        return PortalOrderConfirmationResponse.builder()
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .items(items)
                .customerName(order.getCustomer().getName())
                .customerEmail(order.getCustomer().getEmail())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private String generateOrderNumber() {
        String orderNumber;
        do {
            orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (clientOrderRepository.existsByOrderNumber(orderNumber));
        return orderNumber;
    }
}
