package sn.symmetry.spareparts.service;

import org.springframework.web.multipart.MultipartFile;
import sn.symmetry.spareparts.dto.response.BulkImportResultResponse;

import java.io.IOException;

public interface PartImportService {

    BulkImportResultResponse importParts(MultipartFile file);

    byte[] generateImportTemplate() throws IOException;
}
