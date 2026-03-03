package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.dto.request.CreateCreditNoteRequest;
import sn.symmetry.spareparts.dto.response.CreditNoteResponse;
import sn.symmetry.spareparts.entity.CreditNote;
import sn.symmetry.spareparts.entity.Return;
import sn.symmetry.spareparts.exception.BusinessRuleException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.mapper.CreditNoteMapper;
import sn.symmetry.spareparts.repository.CreditNoteRepository;
import sn.symmetry.spareparts.repository.ReturnRepository;
import sn.symmetry.spareparts.service.CreditNoteService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditNoteServiceImpl implements CreditNoteService {

    private final CreditNoteRepository creditNoteRepository;
    private final ReturnRepository returnRepository;
    private final CreditNoteMapper creditNoteMapper;

    @Override
    public CreditNoteResponse getCreditNote(Long returnId) {
        if (!returnRepository.existsById(returnId)) {
            throw new ResourceNotFoundException("Return", "id", returnId);
        }

        CreditNote creditNote = creditNoteRepository.findByReturnEntityId(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("CreditNote", "returnId", returnId));

        return creditNoteMapper.toResponse(creditNote);
    }

    @Override
    @Transactional
    public CreditNoteResponse createCreditNote(Long returnId, CreateCreditNoteRequest request) {
        Return returnEntity = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return", "id", returnId));

        if (creditNoteRepository.findByReturnEntityId(returnId).isPresent()) {
            throw new BusinessRuleException("A credit note already exists for this return");
        }

        CreditNote creditNote = new CreditNote();
        creditNote.setCreditNoteNumber(generateCreditNoteNumber());
        creditNote.setReturnEntity(returnEntity);
        creditNote.setTotalAmount(request.getTotalAmount());
        creditNote.setIssuedDate(request.getIssuedDate());

        CreditNote saved = creditNoteRepository.save(creditNote);
        return creditNoteMapper.toResponse(saved);
    }

    private String generateCreditNoteNumber() {
        return "CN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
