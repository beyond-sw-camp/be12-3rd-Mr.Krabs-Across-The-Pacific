package com.example.atp_back.portfolio.model.request;

import com.example.atp_back.portfolio.model.entity.Portfolio;
import com.example.atp_back.portfolio.model.entity.Acquisition;
import com.example.atp_back.stock.model.Stock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Schema(description = "포트폴리오를 생성하기 위한 Acquisition 요청 DTO")
public class AcquisitionCreateReqDto {
  @Schema(description = "주식 ID", example = "9")
    private Long stockIdx;
  @Schema(description = "주식 이름", example = "Apple Inc. Common Stock")
    private String stockName;
  @Schema(description= "종목 코드", example = "AAPL")
    private String stockCode;
  @Schema(description = "매입 가격", example = "75000")
    private Long price;
  @Schema(description = "매입 수량", example = "10")
    private BigDecimal quantity;

    public Acquisition toEntity(Stock stock, Portfolio portfolio) {
        return Acquisition.builder()
                .stock(stock)
                .price(price)
                .quantity(quantity)
                .portfolio(portfolio)
                .build();
    }
}
