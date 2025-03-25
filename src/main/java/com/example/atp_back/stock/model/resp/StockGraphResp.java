package com.example.atp_back.stock.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StockGraphResp {
    @Schema(description="종목 이름, 영문명을 그대로 반환")
    private String name;
    @Schema(description="종목 코드")
    private String code;
    @Schema(description="상장 시장: 나스닥, 뉴욕거래소, 아멕스 중 하나")
    private String market;
    @Schema(description="종가, 소수점 두 자리 수까지")
    private List<String> dates;
    @Schema(description="장 마감 날짜, 연-월-일 문자열")
    private List<Double> prices;
}
