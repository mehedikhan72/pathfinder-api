package com.amplifiers.pathfinder.entity.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService service;

    @GetMapping("/find/{transactionId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findByTranxId(@PathVariable String transactionId) {
        return ResponseEntity.ok(service.findByTranxId(transactionId));
    }
}
