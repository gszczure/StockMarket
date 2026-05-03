package org.example.stockmarket.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.stockmarket.dto.OperationLogDto;

import java.util.List;

@Getter
@Builder
public class OperationLogsResponseDto {

    private List<OperationLogDto> log;
}
