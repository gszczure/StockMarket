package org.example.stockmarket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.stockmarket.model.OperationType;

@Getter
@Setter
public class OperationRequestDto {

    @NotNull
    private OperationType type;
}
