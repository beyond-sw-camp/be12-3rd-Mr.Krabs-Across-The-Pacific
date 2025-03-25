package com.example.atp_back.portfolio;

import com.example.atp_back.common.BaseResponse;
import com.example.atp_back.portfolio.model.entity.PortfolioReply;
import com.example.atp_back.portfolio.model.request.PortfolioBookmarkReq;
import com.example.atp_back.portfolio.model.request.PortfolioCreateReqDto;
import com.example.atp_back.portfolio.model.request.PortfolioReplyReq;
import com.example.atp_back.portfolio.model.response.PortfolioInstanceResp;
import com.example.atp_back.portfolio.model.response.PortfolioListResp;
import com.example.atp_back.portfolio.model.response.PortfolioPageResp;
import com.example.atp_back.portfolio.model.response.PortfolioReplyInstanceResp;
import com.example.atp_back.portfolio.service.PortfolioReplyLikesService;
import com.example.atp_back.portfolio.service.PortfolioReplyService;
import com.example.atp_back.portfolio.service.PortfolioService;
import com.example.atp_back.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolio")
@Tag(name="포트폴리오 기능", description = "포트폴리오를 등록하거나 조회하고 북마크하는 등, 포트폴리오와 관련된 기능을 수행")
public class PortfolioController {
  private final PortfolioService portfolioService;
  private final PortfolioReplyService portfolioReplyService;
  private final PortfolioReplyLikesService portfolioReplyLikesService;

  @Operation(summary = "포트폴리오 등록", description = "포트폴리오를 등록하는 기능")
  @PostMapping("/register")
  //TODO : 로그인하지 않았을 경우 로그인페이지로 가도록 추후 수정
  public ResponseEntity<BaseResponse<Long>> register(@AuthenticationPrincipal User user, @RequestBody PortfolioCreateReqDto dto) {
    BaseResponse<Long> resp = BaseResponse.success(portfolioService.register(user, dto));
    return ResponseEntity.ok(resp);
  }

  @Operation(summary = "포트폴리오 리스트 조회", description = "페이지별 포트폴리오 목록을 조회하고, 조회수, 북마크 수, 생성 날짜를 기준으로 정렬")
  @GetMapping("/list")
  public ResponseEntity<BaseResponse<PortfolioPageResp>> list(@AuthenticationPrincipal @Nullable User user,
          @PageableDefault(page = 0, size = 30, sort = "viewCnt") Pageable pageable) {
    BaseResponse<PortfolioPageResp> resp =  BaseResponse.success(portfolioService.list(user, pageable));
    return ResponseEntity.ok(resp);
  }

  @Operation(summary = "특정 유저의 포트폴리오 리스트 조회", description = "유저의 Idx 값을 이용해 해당 유저의 포트폴리오 목록 조회")
  @GetMapping("/list/{userIdx}")
  public ResponseEntity<BaseResponse<PortfolioPageResp>> listByIdx (@AuthenticationPrincipal @Nullable User user, @PageableDefault(page = 0, size = 30, sort = "viewCnt") Pageable pageable, @PathVariable Long userIdx) {
    BaseResponse<PortfolioPageResp> resp =  BaseResponse.success(portfolioService.listByIdx(user, pageable, userIdx));
    return ResponseEntity.ok(resp);
  }

  //유저가 로그인했을 때 해당 유저가 북마크한 포트폴리오 목록 조회

  @Operation(summary = "포트폴리오 상세 조회", description = "포트폴리오의 Idx 값을 이용해 포트폴리오의 상세 내용을 확인하는 기능")
  @GetMapping("/{portfolioIdx}")
  public ResponseEntity<BaseResponse<PortfolioInstanceResp>> read(@AuthenticationPrincipal @Nullable User user, @PathVariable Long portfolioIdx) {
    BaseResponse<PortfolioInstanceResp> resp =  BaseResponse.success(portfolioService.read(user, portfolioIdx));
    return ResponseEntity.ok(resp);
  }

  @Operation(summary = "포트폴리오 댓글 조회", description = "포트폴리오의 Idx 값을 이용해 포트폴리오의 상세 내용 하단의 댓글 목록을 불러오는 기능")
  @GetMapping("/reply/{portfolioIdx}")
  public ResponseEntity<BaseResponse<Slice<PortfolioReplyInstanceResp>>> getReplies(
          @PathVariable Long portfolioIdx,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size) {

    Slice<PortfolioReplyInstanceResp> replyList = portfolioReplyService.getReplies(portfolioIdx, page, size);
    BaseResponse<Slice<PortfolioReplyInstanceResp>> resp = BaseResponse.success(replyList);
    return ResponseEntity.ok(resp);
  }

  @Operation(summary = "포트폴리오 조회수 증가", description = "유저가 포트폴리오를 클릭하면 해당 포트폴리오의 Idx 값을 이용하여 포트폴리오의 조회수를 1 증가시")
  @GetMapping("/view/{portfolioIdx}")
  public void addViewCnt(@PathVariable Long portfolioIdx) {
    portfolioService.viewCnt(portfolioIdx);
  }

  @Operation(summary = "포트폴리오 검색", description = "사용자가 입력한 단어가 포트폴리오의 이름, 주식 이름, 사용자 이름 중 하나라도 일치하는 포트폴리오의 목록을 조회")
  @GetMapping("/search/{keyword}")
  public ResponseEntity<BaseResponse<PortfolioPageResp>> searchByKeyword(@PathVariable String keyword, @AuthenticationPrincipal @Nullable User user, @PageableDefault(page = 0, size = 30) Pageable pageable) {
    BaseResponse<PortfolioPageResp> resp = BaseResponse.success(portfolioService.searchByKeyword(user, pageable, keyword));
    return ResponseEntity.ok(resp);
  }

  @Operation(summary = "포트폴리오 댓글 작성", description = "포트폴리오 댓글을 작성하는 기능")
  @PostMapping("/reply/{portfolioIdx}")
  public ResponseEntity<BaseResponse<Long>> registerReply(
          @AuthenticationPrincipal User user,
          @RequestBody PortfolioReplyReq dto,
          @PathVariable Long portfolioIdx) {
    user.setVersion(0L);
    Long idx = portfolioReplyService.registerReply(dto, user, portfolioIdx);
    BaseResponse<Long> resp = BaseResponse.success(idx);
    return ResponseEntity.ok(resp);
  }

  @Operation(summary = "포트폴리오 댓글 좋아요 누르기", description = "포트폴리오 댓글 좋아요 누르는 기능")
  @PostMapping("/reply/likes/{replyIdx}")
  public ResponseEntity<BaseResponse<Long>> registerReplyLike(
          @AuthenticationPrincipal User user,
          @PathVariable Long replyIdx
  ) {
    Long idx = portfolioReplyLikesService.likesReply(user, replyIdx);
    BaseResponse<Long> resp = BaseResponse.success(idx);
    return ResponseEntity.ok(resp);

  }

  @Operation(summary = "포트폴리오 북마크", description = "포트폴리오에 북마크 버튼을 눌러 북마크하거나 해제하는 기능")
  @PostMapping("/bookmark")
  public ResponseEntity<BaseResponse<Boolean>> registerBookmark(
          @AuthenticationPrincipal User user, @RequestBody PortfolioBookmarkReq portfolioBookmarkReq
  ) {
    Boolean result = portfolioService.registerBookmark(user, portfolioBookmarkReq.getPortfolioIdx(), portfolioBookmarkReq.isBookmark());
      BaseResponse<Boolean> resp = BaseResponse.success(result);
      return ResponseEntity.ok(resp);
  }

  @Operation(summary = "포트폴리오 수익률 계산", description="포트폴리오 수익률 계산을 수동으로 새로고침")
  @GetMapping("/profit/renew")
  public ResponseEntity<BaseResponse<Boolean>> renew(@AuthenticationPrincipal User user) {
    portfolioService.runProfitData();
    return ResponseEntity.ok(BaseResponse.success(true));
  }
}
