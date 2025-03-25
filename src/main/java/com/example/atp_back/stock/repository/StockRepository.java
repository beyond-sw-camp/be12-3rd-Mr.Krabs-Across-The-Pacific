package com.example.atp_back.stock.repository;

import com.example.atp_back.stock.model.Stock;
import com.example.atp_back.stock.model.resp.StockListResp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    public Optional<Stock> findByCode(String code);
    @Query("SELECT new com.example.atp_back.stock.model.resp.StockListResp(s.idx, s.name, s.code, s.market) FROM Stock s")
    Slice<StockListResp> findAllBy(Pageable pageable);
}