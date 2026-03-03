package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarModelResponse implements Serializable {

    private UUID id;
    private String name;
    private UUID brandId;
    private String brandName;
    private Integer yearFrom;
    private Integer yearTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
