package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResultResponse {

    private Integer totalRows;
    private Integer successCount;
    private Integer failureCount;
    private Integer duplicateCount;
    private Long processingTimeMs;

    @Builder.Default
    private List<PartImportErrorResponse> errors = new ArrayList<>();

    @Builder.Default
    private List<PartImportWarningResponse> warnings = new ArrayList<>();

    @Builder.Default
    private List<PartResponse> importedParts = new ArrayList<>();
}
