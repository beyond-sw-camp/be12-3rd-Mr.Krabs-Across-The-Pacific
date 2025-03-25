package com.example.atp_back.stock.service;

import com.example.atp_back.stock.model.resp.StockReplyResp;
import com.example.atp_back.stock.repository.StockReplyRepository;
import com.example.atp_back.stock.model.Stock;
import com.example.atp_back.stock.model.StockReply;
import com.example.atp_back.stock.model.req.StockReplyRegisterReq;
import com.example.atp_back.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockReplyService {
    private final StockReplyRepository stockReplyRepository;

    @Transactional
    public void addReply(StockReplyRegisterReq dto,User user, Long stockIdx) {
        Stock stock = new Stock();
        stock.setIdx(stockIdx);
        user.setVersion(0L);
        StockReply stockReply = dto.toEntity(user, stock);
        stockReplyRepository.save(stockReply);
    }

    @Transactional
    public Slice<StockReplyResp> getStockReply(Long stockIdx, int size, int page, User user) {
        Long userIdx = user==null?null:user.getIdx();
        Slice<StockReplyResp> result = stockReplyRepository.findAllByStockIdxOrderByCreatedAtDesc(stockIdx, userIdx, PageRequest.of(page,size));
        return result;
    }
}
