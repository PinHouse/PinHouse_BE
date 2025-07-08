package com.myhome.server.platform.adapter.in.web.dto.response;

/**
 * [ 사용자 변환에 따른 임대 옵션 응답] DTO
 * - 임대 옵션 정보를 응답할 때 사용합니다.
 * - 추후, 해당 부분은 백엔드의 기능이 아닌, 프런트를 통해 이뤄질 수 있습니다
 *
 * @param id                아이디
 * @param deposit           변환된 보증금
 * @param rent              변환된 월세
 */
public record NoticeLeaseOptionResponse(
        Long id,
        Integer deposit,
        Integer rent
) {

}
