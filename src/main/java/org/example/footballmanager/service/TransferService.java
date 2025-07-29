package org.example.footballmanager.service;

import org.example.footballmanager.dto.TransferRequestDto;

public interface TransferService {
    void performTransfer(TransferRequestDto dto);
}
