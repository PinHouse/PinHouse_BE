package com.pinHouse.server.core.response.response.pageable;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
@Tag(name = "무한스크롤 응답 DTO")
public record SliceResponse<T>(
        List<T> content,
        boolean hasNext,
        int page,
        int size
) {
    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return SliceResponse.<T>builder()
                .content(slice.getContent())
                .hasNext(slice.hasNext())
                .page(slice.getNumber() + 1)
                .size(slice.getSize())
                .build();
    }
}
