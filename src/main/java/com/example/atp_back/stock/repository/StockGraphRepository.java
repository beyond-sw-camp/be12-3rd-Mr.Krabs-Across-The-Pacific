package com.example.atp_back.stock.repository;

import com.example.atp_back.stock.model.StockGraphDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StockGraphRepository extends MongoRepository<StockGraphDocument, String> {
    public Iterable<StockGraphDocument> findAllByCodeOrderByDateAsc(String code);
    public Optional<StockGraphDocument> findFirstByCodeOrderByDateDesc(String code);
    public List<StockGraphDocument> findAllByDateBetweenOrderByCodeAsc(Long start, Long end);
}
