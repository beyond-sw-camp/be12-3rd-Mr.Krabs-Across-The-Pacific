package com.example.atp_back.portfolio.repository;

import com.example.atp_back.portfolio.model.entity.Portfolio;
import com.example.atp_back.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long>, PortfolioCustomRepository{
    Page<Portfolio> findAllByOrderByViewCntDesc(Pageable pageable);
    Page<Portfolio> findAllByOrderByCreatedAtDesc(Pageable pageable);
}