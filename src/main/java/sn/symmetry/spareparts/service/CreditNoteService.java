package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.request.CreateCreditNoteRequest;
import sn.symmetry.spareparts.dto.response.CreditNoteResponse;

import java.util.UUID;

public interface CreditNoteService {

    CreditNoteResponse getCreditNote(UUID returnId);

    CreditNoteResponse createCreditNote(UUID returnId, CreateCreditNoteRequest request);
}
