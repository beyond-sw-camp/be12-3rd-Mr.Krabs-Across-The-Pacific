package com.example.atp_back.common;

import com.example.atp_back.portfolio.model.response.PortfolioInstanceResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<T> instances;

    public static <T> PageResponse<T> from(Page page, List<T> dto) {
        return PageResponse.<T>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .instances(dto)
                .build();
    }

    public static <T> PageResponse<T> from(Slice<T> slice) {
      return PageResponse.<T>builder()
          .page(slice.getNumber())
          .size(slice.getSize())
          .hasNext(slice.hasNext())
          .hasPrevious(slice.hasPrevious())
          .instances(slice.getContent())
          .build();
    }
}
