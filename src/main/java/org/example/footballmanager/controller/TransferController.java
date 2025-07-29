package org.example.footballmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.footballmanager.dto.TransferRequestDto;
import org.example.footballmanager.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void performTransfer(@RequestBody @Valid TransferRequestDto dto) {
        transferService.performTransfer(dto);
    }
}