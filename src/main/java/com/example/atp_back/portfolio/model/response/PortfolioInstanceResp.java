package com.example.atp_back.portfolio.model.response;

import com.example.atp_back.portfolio.model.entity.Badge;
import com.example.atp_back.portfolio.model.entity.Portfolio;
import com.example.atp_back.portfolio.model.entity.PortfolioReply;
import com.example.atp_back.portfolio.model.entity.Reward;
import com.example.atp_back.stock.model.StockGraphDocument;
import com.example.atp_back.user.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "포트폴리오 상세 정보")
public class PortfolioInstanceResp {
  @Schema(description = "포트폴리오 ID", example = "1")
    private Long idx;
  @Schema(description = "포트폴리오를 생성한 유저의 Idx", example = "101")
    private Long userIdx;
  @Schema(description = "포트폴리오 이름", example = "Crab's Portfolio")
    private String name;
  @Schema(description = "포트폴리오 수익률", example = "3.85")
    private Double profit;
  @Schema(description = "포트폴리오 순위", example = "19")
    private Integer ratings;
  @Schema(description = "생성된 포트폴리오의 정보를 담은 이미지")
    private String imageUrl;
  @Schema(description = "포트폴리오가 조회된 횟수", example = "100")
    private int viewCnt;
  @Schema(description = "포트폴리오가 북마크되었는지 여부", example = "false")
    private boolean bookmark;
  @Schema(description = "포트폴리오가 북마크된 횟수", example = "30")
    private int bookmarkCnt;
  @Schema(description = "포트폴리오에 부여된 뱃지 정보", example = "101")
    private int badges;
  @Schema(description = "포트폴리오를 북마크한 유저 목록")
    private List<Long> bookmarkUsers = new ArrayList<>();
  @Schema(description = "포트폴리오에 등록된 구매 주식 리스트")
    private List<AcquisitionInstanceResp> acquisitionList = new ArrayList<>();
    private boolean isOwn;
    private String profileImage;
    private List<StockSummaryResp> topStocks;

  public static PortfolioInstanceResp fromMain(@Nullable User user, PortfolioInstanceResp portfolioResp) {
    return PortfolioInstanceResp.builder()
        .idx(portfolioResp.getIdx())
        .name(portfolioResp.getName())
        .imageUrl(portfolioResp.getImageUrl())
        .viewCnt(portfolioResp.getViewCnt())
        .badges(portfolioResp.getBadges())
        .bookmark(user != null && portfolioResp.getBookmarkUsers().contains(user.getIdx())) //북마크 여부 확인
        .bookmarkCnt(portfolioResp.getBookmarkCnt()) // 북마크 개수
        .acquisitionList(portfolioResp.getAcquisitionList())
        .build();
  }

  //포트폴리오 수정 페이지 응답
  public static PortfolioInstanceResp fromUpdate(User user, Portfolio portfolio) {
    return PortfolioInstanceResp.builder()
        .idx(portfolio.getIdx())
        .name(portfolio.getName())
        .acquisitionList(portfolio.getAcquisitionList().stream().map(AcquisitionInstanceResp::from).collect(Collectors.toList()))
        .build();
  }

  //포트폴리오 상세 페이지 응답
  public static PortfolioInstanceResp fromDetail(User user, Portfolio portfolio) {
    List<AcquisitionInstanceResp> acquisitionList = portfolio.getAcquisitionList().stream()
        .map(AcquisitionInstanceResp::from)
        .toList();

    // 상위 5개 주식 계산
    List<StockSummaryResp> topStocks = getTop5Stocks(acquisitionList);

    return PortfolioInstanceResp.builder()
        .idx(portfolio.getIdx())
        .name(portfolio.getName())
        .userIdx(portfolio.getUser().getIdx())
        .profileImage(portfolio.getUser().getProfileImage())
        .isOwn(user!=null && user.getIdx().equals(portfolio.getUser().getIdx()))// 포트폴리오 소유 여부 확인
        .topStocks(topStocks).acquisitionList(acquisitionList)
        .profit(portfolio.getProfit())
        .ratings(portfolio.getRatings())
        .build();
  }

  // 주식별 stockPrice 합산 후 상위 5개만 반환하는 함수
  private static List<StockSummaryResp> getTop5Stocks(List<AcquisitionInstanceResp> acquisitions) {
    // 1. 주식별 stockPrice 합산 (quantity * price)
    Map<String, BigDecimal> stockPriceMap = acquisitions.stream()
        .collect(Collectors.groupingBy(
            AcquisitionInstanceResp::getStockName,
            Collectors.reducing(BigDecimal.ZERO,
                a -> a.getQuantity().multiply(BigDecimal.valueOf(a.getPrice())),
                BigDecimal::add)
        ));

    // 2. 전체 stockPrice 합산 (퍼센트 계산을 위해)
    BigDecimal totalStockPrice = stockPriceMap.values().stream()
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // 3. stockPrice 기준 내림차순 정렬
    List<Map.Entry<String, BigDecimal>> sortedStocks = stockPriceMap.entrySet().stream()
        .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
        .toList();

    // 4. 상위 4개 주식 선택
    List<StockSummaryResp> topStocks = sortedStocks.stream()
        .limit(4)
        .map(entry -> new StockSummaryResp(
            entry.getKey(),
            entry.getValue(),
            calculatePercentage(entry.getValue(), totalStockPrice)
        ))
        .collect(Collectors.toList());

    // 5. 나머지 주식 합산하여 "Others"로 처리
    BigDecimal othersStockPrice = sortedStocks.stream()
        .skip(4) // 상위 4개 제외
        .map(Map.Entry::getValue)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (othersStockPrice.compareTo(BigDecimal.ZERO) > 0) {
      topStocks.add(new StockSummaryResp(
          "Others",
          othersStockPrice,
          calculatePercentage(othersStockPrice, totalStockPrice)
      ));
    }

    return topStocks;
  }

  // 퍼센트 계산 함수
  private static double calculatePercentage(BigDecimal part, BigDecimal total) {
    if (total.compareTo(BigDecimal.ZERO) == 0) { return 0.0; }
    return part.multiply(BigDecimal.valueOf(100))
        .divide(total, 2, RoundingMode.HALF_UP).doubleValue();
  }
}
