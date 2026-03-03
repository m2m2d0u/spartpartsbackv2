package sn.symmetry.spareparts.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.symmetry.spareparts.config.MapStructConfig;
import sn.symmetry.spareparts.dto.response.PaymentResponse;
import sn.symmetry.spareparts.entity.Payment;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface PaymentMapper {

    @Mapping(source = "invoice.id", target = "invoiceId")
    PaymentResponse toResponse(Payment payment);

    List<PaymentResponse> toResponseList(List<Payment> payments);
}
