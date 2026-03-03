package sn.symmetry.spareparts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.symmetry.spareparts.dto.request.CreateCreditNoteRequest;
import sn.symmetry.spareparts.dto.response.CreditNoteResponse;
import sn.symmetry.spareparts.dto.response.common.ApiResponse;
import sn.symmetry.spareparts.service.CreditNoteService;

import java.util.UUID;

@RestController
@RequestMapping("/api/returns/{returnId}/credit-note")
@RequiredArgsConstructor
public class CreditNoteController {

    private final CreditNoteService creditNoteService;

    @GetMapping
    public ResponseEntity<ApiResponse<CreditNoteResponse>> getCreditNote(
            @PathVariable UUID returnId) {
        return ResponseEntity.ok(ApiResponse.success(creditNoteService.getCreditNote(returnId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreditNoteResponse>> createCreditNote(
            @PathVariable UUID returnId,
            @Valid @RequestBody CreateCreditNoteRequest request) {
        CreditNoteResponse response = creditNoteService.createCreditNote(returnId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Credit note created successfully", response));
    }
}
