package co.kr.pinhouse.domain.housing.complex.application.util;

import java.io.UnsupportedEncodingException;

import co.kr.pinhouse.domain.housing.complex.application.dto.result.PathResult;

/**
 * [외부 거리계산]을 위한 인터페이스입니다.
 */
public interface DistanceUtil {

	/// 응답
	PathResult findPathResult(double startY, double startX, double endY, double endX)
		throws UnsupportedEncodingException;

}
