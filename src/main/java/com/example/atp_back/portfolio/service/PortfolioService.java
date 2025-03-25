package com.example.atp_back.portfolio.service;

import com.example.atp_back.common.code.status.ErrorStatus;
import com.example.atp_back.common.exception.handler.PortfolioHandler;
import com.example.atp_back.common.exception.handler.UserHandler;
import com.example.atp_back.portfolio.model.entity.*;
import com.example.atp_back.portfolio.model.request.PortfolioCreateReqDto;
import com.example.atp_back.portfolio.model.response.*;
import com.example.atp_back.portfolio.repository.*;
import com.example.atp_back.stock.model.Stock;
import com.example.atp_back.stock.repository.StockRepository;
import com.example.atp_back.user.model.User;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final BadgeRepository badgeRepository;
    private final RewardRepository rewardRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PortfolioReplyService portfolioReplyService;
    private final AcquisitionRepository acquisitionRepository;
    private final StockRepository stockRepository;

    @Transactional
    public Long register(User user, PortfolioCreateReqDto dto) {
        Portfolio portfolio = portfolioRepository.save(dto.toEntity(user));
        dto.getAcquisitionList().forEach(acquisition -> {
            Stock stock = stockRepository.findByCode(acquisition.getStockCode()).orElseThrow(()-> new PortfolioHandler(ErrorStatus._BAD_REQUEST));
            acquisitionRepository.save(acquisition.toEntity(stock, portfolio));
        });
        return portfolio.getIdx();
    }

    public PortfolioPageResp list(@Nullable User user, Pageable pageable) {
        //메인 페이지에 보여줄 정렬 조건
        String sortBy = pageable.getSort().stream().findFirst().map(Sort.Order::getProperty).orElse("View");
        return PortfolioPageResp.from(user, portfolioRepository.findAllByOrderByKeyword(pageable, sortBy));
    }

    public PortfolioPageResp listByIdx(@Nullable User user, Pageable pageable, Long userIdx) {
      String sortBy = pageable.getSort().stream().findFirst().map(Sort.Order::getProperty).orElse("View");
      return PortfolioPageResp.from(user, portfolioRepository.findAllByOrderByUserId(pageable, sortBy, userIdx));
    }

    public PortfolioInstanceResp read(@Nullable User user, Long portfolioIdx) {
        //포트폴리오 idx를 이용해서 acquisition 목록 반환
        Portfolio portfolio = portfolioRepository.findWithAcquisitionsById(portfolioIdx);
        return PortfolioInstanceResp.fromDetail(user, portfolio);
    }

    /*포트폴리오 검색*/
    public PortfolioPageResp searchByKeyword(@Nullable User user, Pageable pageable, String keyword) {
        return PortfolioPageResp.from(user, portfolioRepository.searchAllByKeyword(pageable, keyword));
    }

    /*포트폴리오 조회수 관련*/
    @Transactional
    public void viewCnt(Long portfolioIdx) {
        //조회수 증가
        portfolioRepository.incrementViewCnt(portfolioIdx);

        Portfolio portfolio = portfolioRepository.findById(portfolioIdx).orElseThrow();
        int viewCnt = portfolio.getViewCnt();
        //조회수가 1000회 이상이면 1번 뱃지 부여
        if (viewCnt > 1000){ assignBadge(portfolio, 1); }
    }

    /*포트폴리오 북마크*/
    @Transactional
    public Boolean registerBookmark(User user, Long portfolioIdx, boolean bookmark) {
        Portfolio portfolio = portfolioRepository.findById(portfolioIdx).orElseThrow();
        if (!bookmark) {
            //북마크 추가
            Bookmark newBookmark = Bookmark.builder().user(user).portfolio(portfolio).build();
            bookmarkRepository.save(newBookmark);
            int bookmarkCnt = bookmarkRepository.countByPortfolioIdx(portfolioIdx);
            //북마크 수가 100개 이상이면 해당 포트폴리오에 2번 뱃지 부여
            if(bookmarkCnt > 100){ assignBadge(portfolio, 2); }
            return true;
        } else { //북마크 취소
            Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserAndPortfolio(user, portfolio);
            existingBookmark.ifPresent(bookmarkRepository::delete);
            return false;
        }
    }

    @Transactional
    public void assignBadge(Portfolio portfolio, long badgeIdx) {
        Badge badge = badgeRepository.findById(badgeIdx).orElseThrow();

        // 이미 같은 Badge가 있으면 중복 방지
        boolean alreadyHasBadge = rewardRepository.existsByPortfolioAndBadge(portfolio, badge);
        if (alreadyHasBadge) { return; }

        //포트폴리오와 badge에 업데이트된 정보 저장
        portfolioRepository.save(Portfolio.builder().badges(portfolio.getBadges()+badge.getCode()).build());
        rewardRepository.save(Reward.builder().portfolio(portfolio).badge(badge).build());
    }

//    @Transactional
//    public void badgeToPortfolio(Portfolio portfolio){
//        //포트폴리오 생성 기간 계산
//        long portfolioSinceCreated = Duration.between(portfolio.getCreatedAt(), LocalDateTime.now()).toDays();
//        }
//        //TODO : 추후 수익률 구현되면 3번 뱃지 추가하기 (생성 기간과 수익률 정보 같이 적용)
//        if(portfolioSinceCreated > 30) {
//            assignBadge(portfolio, 3);
//        }
//    }
}
