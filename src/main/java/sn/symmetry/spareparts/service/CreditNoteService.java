package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.request.CreateCreditNoteRequest;
import sn.symmetry.spareparts.dto.response.CreditNoteResponse;

public interface CreditNoteService {

    CreditNoteResponse getCreditNote(Long returnId);

    CreditNoteResponse createCreditNote(Long returnId, CreateCreditNoteRequest request);
}
