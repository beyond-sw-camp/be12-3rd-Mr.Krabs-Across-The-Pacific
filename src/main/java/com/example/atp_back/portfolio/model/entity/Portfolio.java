package com.example.atp_back.portfolio.model.entity;


import com.example.atp_back.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String name;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String imageUrl; //메인 페이지에서 포트폴리오 이미지를 띄우기 위해 추가(현재 ERD에는 없음)
    private int viewCnt;
    private int badges; //메인 페이지에서 포트폴리오의 뱃지 목록을 가져오기 위한 반정규화
    private Double profit; // 수익률 기록하는 반정규화 컬럼 + Setter 어노테이션이 있어야 하는 이유
    private int ratings; // 하루에 한 번 정기적으로 갱신되는 ratings

    public void addViewCount() { this.viewCnt ++; }
    @ManyToOne
    @JoinColumn(name="user_idx")
    private User user;

    @OneToMany(mappedBy = "portfolio")
    private List<Acquisition> acquisitionList;

    @OneToMany(mappedBy = "portfolio")
    private List<Bookmark> bookmarkList;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "portfolio")
    private List<PortfolioReply> portfolioReplyList;

    @OneToMany(mappedBy = "portfolio")
    private List<Reward> rewards;
}