package org.example.stockmarket.service;

import org.example.stockmarket.dto.response.OperationLogsResponseDto;
import org.example.stockmarket.mapper.OperationLogMapper;
import org.example.stockmarket.model.OperationLog;
import org.example.stockmarket.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogService {

    private final AuditLogRepository auditLogRepository;

    public OperationLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String type, String walletId, String stockName) {
        OperationLog log = OperationLog.builder()
                .type(type)
                .walletId(walletId)
                .stockName(stockName)
                .build();

        auditLogRepository.save(log);
    }

    public OperationLogsResponseDto getAllLogs() {
        List<OperationLog> logs = auditLogRepository.findAll();
        return OperationLogMapper.toResponse(logs);
    }
}
