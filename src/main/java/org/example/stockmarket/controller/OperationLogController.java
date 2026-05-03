package org.example.stockmarket.controller;

import org.example.stockmarket.dto.response.OperationLogsResponseDto;
import org.example.stockmarket.service.OperationLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
public class OperationLogController {

    private final OperationLogService auditLogService;

    public OperationLogController(OperationLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<OperationLogsResponseDto> getLog() {
        return ResponseEntity
                .ok(auditLogService.getAllLogs());
    }
}
