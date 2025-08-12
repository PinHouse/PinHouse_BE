package com.pinHouse.server.platform.adapter.in.web.dto.response;

import com.pinHouse.server.platform.domain.facility.Library;
import com.pinHouse.server.platform.domain.notice.NoticeInfra;
import io.micrometer.common.lang.Nullable;
import lombok.Builder;
import java.util.List;

/**
 * 인프라 관련 응답 DTO 클래스입니다.
 */
public record InfraDTO() {

    /**
     * 도서관 정보 응답 객체입니다.
     *
     * @param libraryCode    도서관 코드
     * @param libraryName    도서관 이름
     * @param libraryAddress 도서관 주소
     */

    @Builder
    public record LibraryResponse(
            String libraryCode,
            String libraryName,
            String libraryAddress
    ) {

        /// 정적 팩토리 메서드
        public static LibraryResponse from(Library library) {
            return LibraryResponse.builder()
                    .libraryCode(library.getId())
                    .libraryName(library.getName())
                    .libraryAddress(library.getAddress())
                    .build();
        }

        /// 정적 팩토리 메서드
        public static List<LibraryResponse> from(List<Library> libraries) {
            return libraries.stream()
                    .map(LibraryResponse::from)
                    .toList();
        }
    }

    /**
     * 공지 인프라 통계 응답 객체입니다.
     *
     * @param libraries 도서관 응답 목록
     */
    @Builder
    public record NoticeInfraResponse(
            @Nullable List<LibraryResponse> libraries

    ) {

        /// 정적 팩토리 메서드
        public static NoticeInfraResponse from(NoticeInfra noticeInfra) {
            return NoticeInfraResponse.builder()
                    .libraries(LibraryResponse.from(noticeInfra.getNearbyLibraries()))
                    .build();
        }


    }
}
