package com.example.atp_back.stock;

import com.example.atp_back.common.BaseResponse;
import com.example.atp_back.stock.model.req.StockReplyRegisterReq;
import com.example.atp_back.stock.model.resp.StockDetailResp;
import com.example.atp_back.stock.model.resp.StockListResp;
import com.example.atp_back.stock.service.StockGraphService;
import com.example.atp_back.stock.service.StockReplyLikesService;
import com.example.atp_back.stock.service.StockReplyService;
import com.example.atp_back.stock.service.StockService;
import com.example.atp_back.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.atp_back.common.BaseResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
@Tag(name = "주식 관련 기능")
public class StockController {
    private final StockService stockService;
    private final StockReplyService stockReplyService;
    private final StockReplyLikesService stockReplyLikesService;
    private final StockGraphService stockGraphService;

    @Operation(summary = "주식 상세페이지 조회", description = """
            /stock/detail/{idx} 값을 입력 받는다. \n
            idx 값을 가지는 주식의 id, 주식 이름, 주식 코드, 주식 거래소를 반환하며 \n
            해당 주식의 댓글들을 반환 받는다. \n
            주식의 댓글은, 댓글의 id, 주식의 id, 작성자의 id, 작성자의 이름, 댓글 내용, 댓글 작성 시간, 댓글 수정 시간, 좋아요 개수를 전달받는다.
            """)
    @GetMapping("/detail/{idx}")
    public ResponseEntity<BaseResponse<StockDetailResp>> getStock(@PathVariable @Valid @PositiveOrZero Long idx) {
        BaseResponse<StockDetailResp> resp = BaseResponse.success(stockService.getStock(idx));
        return ResponseEntity.ok(resp);
    }

    // TODO : 주식 목록 페이지네이션, 주식 반환값 확정
    @Operation(summary = "주식 목록 조회", description = """
            /stock/list 값을 입력 받는다. \n
            주식들의 id, 주식 이름, 주식 코드, 주식 거래소 값들을 반환한다.
            """)
    @GetMapping("/list")
    public ResponseEntity<BaseResponse<List<StockListResp>>> getStocks() {
        BaseResponse<List<StockListResp>> resp = BaseResponse.success(stockService.getAllStocks());
        return ResponseEntity.ok(resp);
    }


    @Operation(summary = "주식 댓글 작성", description = """
            /stock/reply/{stockId} 값을 입력 받는다. 인가된 사용자만 사용할 수 있다. \n
            contents 값을 전달 받아서 인가 사용자와 입력받은 주식 id 값을 바탕으로 댓글을 저장한다.
            """)
    @PostMapping("/reply/{stockId}")
    public ResponseEntity<BaseResponse<String>> PostStockReply(@RequestBody @Valid StockReplyRegisterReq dto,
                                                              @AuthenticationPrincipal @Valid @NotNull User user,
                                                              @PathVariable @Valid @PositiveOrZero Long stockId) {
        stockReplyService.addReply(dto, user, stockId);
        BaseResponse<String> resp = BaseResponse.success("success");
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "주식 댓글 좋아요 누르기", description = """
            /stock/reply/likes/{replyId} 값을 전달받는다. 인가된 사용자만 사용할 수 있다. \n
            인가 사용자와 입력받은 댓글 id 값을 바탕으로 좋아요를 저장한다.
            """)
    @PostMapping("/reply/likes/{replyId}")
    public ResponseEntity<BaseResponse<String>> LikeReply(@AuthenticationPrincipal @Valid @NotNull User user,
                                                           @PathVariable @Valid @PositiveOrZero Long replyId) {
        stockReplyLikesService.likeReply(user, replyId);
        BaseResponse<String> resp = BaseResponse.success("success");
        return ResponseEntity.ok(resp);
    }
    @Operation(summary="종목의 최신 가격 구하기", description="/recent/{code} URI로 종목 코드 문자열을 code에 받아와 Double 타입으로 최근 가격을 달러 단위로 반환한다.")
    @GetMapping("/recent/{code}")
    public ResponseEntity<BaseResponse<Double>> getRecentPrice(@Valid @NotBlank @PathVariable String code) {
        Double result = stockGraphService.getRecentPrice(code);
        BaseResponse<Double> resp = BaseResponse.<Double>builder().isSuccess(true).result(result).build();
        return ResponseEntity.ok(resp);
    }

}
