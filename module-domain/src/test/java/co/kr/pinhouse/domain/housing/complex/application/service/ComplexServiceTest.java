package co.kr.pinhouse.domain.housing.complex.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.kr.pinhouse.domain.Location;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.ChipType;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.DistanceResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.TransitInfoResponse;
import co.kr.pinhouse.domain.housing.complex.application.util.DistanceUtil;
import co.kr.pinhouse.domain.housing.complex.application.util.TransitResponseMapper;
import co.kr.pinhouse.domain.housing.complex.domain.entity.ComplexDocument;
import co.kr.pinhouse.domain.housing.complex.domain.repository.ComplexDocumentRepository;
import co.kr.pinhouse.domain.housing.complex.domain.transit.PathResult;
import co.kr.pinhouse.domain.housing.complex.domain.transit.RootResult;
import co.kr.pinhouse.domain.housing.facility.application.usecase.FacilityUseCase;
import co.kr.pinhouse.domain.like.application.usecase.LikeQueryUseCase;
import co.kr.pinhouse.domain.pinpoint.application.usecase.PinPointUseCase;
import co.kr.pinhouse.domain.pinpoint.domain.entity.PinPoint;

@ExtendWith(MockitoExtension.class)
class ComplexServiceTest {

	@Mock
	private ComplexDocumentRepository repository;

	@Mock
	private PinPointUseCase pinPointService;

	@Mock
	private DistanceUtil distanceUtil;

	@Mock
	private TransitResponseMapper mapper;

	@Mock
	private LikeQueryUseCase likeService;

	@Mock
	private FacilityUseCase facilityService;

	@Mock
	private DistanceCacheService distanceCacheService;

	@InjectMocks
	private ComplexService complexService;

	@Test
	void getTransitInfo_usesTransitInfoCacheBeforeRecalculation() throws UnsupportedEncodingException {
		TransitInfoResponse cachedTransitInfo = TransitInfoResponse.builder()
			.totalTime("1시간 10분")
			.totalTimeMinutes(70)
			.totalDistance(21.4)
			.segments(List.of())
			.build();

		when(distanceCacheService.getTransitInfo("complex-1", "pin-1")).thenReturn(cachedTransitInfo);

		TransitInfoResponse result = complexService.getTransitInfo("complex-1", "pin-1");

		assertThat(result).isSameAs(cachedTransitInfo);
		verify(distanceUtil, never()).findPathResult(anyDouble(), anyDouble(), anyDouble(), anyDouble());
		verify(repository, never()).findById(any());
		verify(distanceCacheService, never()).getRootResult(any(), any());
	}

	@Test
	void getTransitInfo_recalculatesWhenTransitInfoCacheIsMissing() throws UnsupportedEncodingException {
		ComplexDocument complex = mock(ComplexDocument.class);
		when(complex.getLocation()).thenReturn(Location.of(127.0, 37.5));
		when(repository.findById("complex-1")).thenReturn(Optional.of(complex));

		PinPoint pinPoint = PinPoint.of("user-1", "서울", "집", 37.6, 127.1, true);
		when(pinPointService.loadPinPoint("pin-1")).thenReturn(pinPoint);

		PathResult pathResult = mock(PathResult.class);
		RootResult rootResult = RootResult.builder()
			.totalTime(25)
			.totalPayment(1450)
			.totalDistance(8200)
			.steps(List.of())
			.build();
		TransitInfoResponse transitInfo = TransitInfoResponse.builder()
			.totalTime("25분")
			.totalTimeMinutes(25)
			.totalDistance(8.2)
			.segments(List.of())
			.build();

		when(distanceCacheService.getTransitInfo("complex-1", "pin-1")).thenReturn(null);
		when(distanceUtil.findPathResult(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(pathResult);
		when(pathResult.routes()).thenReturn(List.of(rootResult));
		when(mapper.selectBest(pathResult)).thenReturn(rootResult);
		when(mapper.toTransitInfoResponse(rootResult)).thenReturn(transitInfo);

		TransitInfoResponse result = complexService.getTransitInfo("complex-1", "pin-1");

		assertThat(result).isSameAs(transitInfo);
		verify(distanceUtil).findPathResult(eq(37.6), eq(127.1), eq(37.5), eq(127.0));
		verify(distanceCacheService).cacheRootResult("complex-1", "pin-1", rootResult);
		verify(distanceCacheService).cacheTransitInfo("complex-1", "pin-1", transitInfo);
		verify(distanceCacheService, never()).getRootResult(any(), any());
	}

	@Test
	void getEasyDistance_warmsTransitInfoCacheOnFreshCalculation() throws UnsupportedEncodingException {
		ComplexDocument complex = mock(ComplexDocument.class);
		when(complex.getLocation()).thenReturn(Location.of(127.0, 37.5));
		when(repository.findById("complex-1")).thenReturn(Optional.of(complex));

		PinPoint pinPoint = PinPoint.of("user-1", "서울", "집", 37.6, 127.1, true);
		when(pinPointService.loadPinPoint("pin-1")).thenReturn(pinPoint);

		PathResult pathResult = mock(PathResult.class);
		RootResult rootResult = RootResult.builder()
			.totalTime(25)
			.totalPayment(1450)
			.totalDistance(8200)
			.steps(List.of())
			.build();
		TransitInfoResponse transitInfo = TransitInfoResponse.builder()
			.totalTime("25분")
			.totalTimeMinutes(25)
			.totalDistance(8.2)
			.segments(List.of())
			.build();
		List<DistanceResponse.TransitResponse> routes = List.of(
			DistanceResponse.TransitResponse.builder()
				.type(ChipType.WALK)
				.build()
		);

		when(distanceCacheService.getRootResult("complex-1", "pin-1")).thenReturn(null);
		when(distanceUtil.findPathResult(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(pathResult);
		when(pathResult.routes()).thenReturn(List.of(rootResult));
		when(mapper.selectBest(pathResult)).thenReturn(rootResult);
		when(mapper.toTransitInfoResponse(rootResult)).thenReturn(transitInfo);
		when(mapper.from(rootResult)).thenReturn(routes);

		DistanceResponse result = complexService.getEasyDistance("complex-1", "pin-1");

		assertThat(result.totalTimeMinutes()).isEqualTo(25);
		verify(distanceCacheService).cacheRootResult("complex-1", "pin-1", rootResult);
		verify(distanceCacheService).cacheTransitInfo("complex-1", "pin-1", transitInfo);
	}
}
