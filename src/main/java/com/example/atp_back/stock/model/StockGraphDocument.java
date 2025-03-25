package com.example.atp_back.stock.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection="stock")
public class StockGraphDocument {
    @Id
    private String ObjectId;
    @Schema(description="고유 id, 종목 코드랑 상관 없음")
    private Integer id;
    @Schema(description="종목 이름, 영문명을 그대로 반환")
    private String name;
    @Schema(description="종목 코드")
    private String code;
    @Schema(description="상장 시장: 나스닥, 뉴욕거래소, 아멕스 중 하나")
    private String market;
    @Schema(description="종가")
    private Double price;
    @Schema(description="장 마감 날짜, 타임스탬프 형식")
    private Long date;
}
