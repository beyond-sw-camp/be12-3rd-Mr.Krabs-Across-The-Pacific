package com.example.atp_back.portfolio.model.response;

import com.example.atp_back.portfolio.model.entity.Acquisition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "포트폴리오에 등록된 구매 주식의 개별 정보")
public class AcquisitionInstanceResp {
  @Schema(description = "구매 ID", example = "11")
    private Long idx;
  @Schema(description = "매입 가격", example = "75000")
    private Long price;
  @Schema(description = "매입 수량", example = "10")
    private BigDecimal quantity;
  @Schema(description = "매입 날짜", example = "10")
    private LocalDateTime orderAt;
  @Schema(description = "구매 정보가 등록된 포트폴리오 Idx", example = "10")
    private Long portfolioIdx;
  @Schema(description = "주식 ID", example = "9")
    private Long stockIdx;
  @Schema(description = "주식 코드", example = "AAPL")
  private String stockCode;
  @Schema(description = "주식 이름", example = "Apple Inc. Common Stock")
    private String stockName;
  @Schema(description = "메인 페이지에서 수익률 계산을 위한 주식 구매 총합산 금액", example = "750000")
    private BigDecimal stockPrice;

    public static AcquisitionInstanceResp from(Acquisition acquisition) {
        return AcquisitionInstanceResp.builder()
                .idx(acquisition.getIdx())
                .price(acquisition.getPrice())
                .quantity(acquisition.getQuantity())
                .orderAt(acquisition.getOrderAt())
                .stockCode(acquisition.getStock().getCode())
                .stockName(acquisition.getStock().getName())
                .stockCode(acquisition.getStock().getCode())
                .build();
    }
}
