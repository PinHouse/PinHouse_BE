package co.kr.pinhouse.domain.like;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.like.application.dto.LikeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "좋아요 API", description = "좋아요 생성/조회/삭제 API 입니다.")
public interface LikeApiSpec {

	/// 좋아요 생성
	@Operation(
			summary = "좋아요 생성",
			description = "좋아요를 생성하는 API 입니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							examples = {
									@ExampleObject(name = "공고 좋아요 예시", value = NOTICE_EXAMPLE),
									@ExampleObject(name = "임대주택 좋아요 예시", value = COMPLEX_EXAMPLE),
							}
					)
			)
	)
	ApiResponse<Void> like(
			@RequestBody @Valid LikeRequest request,
			@CurrentUserId(required = true) UUID userId);


	/// 좋아요 취소
	@Operation(
			summary = "좋아요 취소",
			description = "좋아요를 취소하는 API 입니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							examples = {
									@ExampleObject(name = "공고 좋아요 취소 예시", value = NOTICE_EXAMPLE),
									@ExampleObject(name = "임대주택 좋아요 취소 예시", value = COMPLEX_EXAMPLE),
							}
					)
			)
	)
	ApiResponse<Void> disLike(
			@RequestBody @Valid LikeRequest request,
			@CurrentUserId(required = true) UUID userId);

	/// 공고 좋아요 예시
	String NOTICE_EXAMPLE = """
			{
				"targetId": "19417",
				"type": "NOTICE"
			}

			""";

	/// 방 좋아요 예시
	String COMPLEX_EXAMPLE = """
			{
			"targetId": "4b30ca7d718d4ea9a9f6966f",
			"type": "ROOM"
			}
			""";
}
