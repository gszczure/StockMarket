package org.example.stockmarket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.example.stockmarket.model.OperationType;

@Getter
public class OperationRequestDto {

    @NotNull
    private OperationType type;
}
