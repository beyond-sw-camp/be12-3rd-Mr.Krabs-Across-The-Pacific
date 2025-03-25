package com.example.atp_back.stock.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockGraphReq {
    @Schema(description="종목 코드의 배열")
    @NotBlank
    private List<String> codes;
}
