package co.kr.pinhouse.domain.housing.notice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.kr.pinhouse.domain.housing.complex.application.service.DistanceCacheService;
import co.kr.pinhouse.domain.housing.complex.application.usecase.ComplexUseCase;
import co.kr.pinhouse.domain.housing.complex.domain.entity.Address;
import co.kr.pinhouse.domain.housing.complex.domain.entity.ComplexDocument;
import co.kr.pinhouse.domain.housing.complex.domain.entity.Deposit;
import co.kr.pinhouse.domain.housing.complex.domain.entity.UnitType;
import co.kr.pinhouse.domain.housing.facility.application.usecase.FacilityUseCase;
import co.kr.pinhouse.domain.housing.notice.application.dto.UnitTypeSortType;
import co.kr.pinhouse.domain.housing.notice.application.dto.response.UnitTypeCompareResponse;
import co.kr.pinhouse.domain.housing.notice.domain.entity.NoticeDocument;
import co.kr.pinhouse.domain.housing.notice.domain.repository.NoticeDocumentRepository;
import co.kr.pinhouse.domain.like.application.usecase.LikeQueryUseCase;
import co.kr.pinhouse.domain.pinpoint.application.usecase.PinPointUseCase;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

	@Mock
	private NoticeDocumentRepository repository;

	@Mock
	private ComplexUseCase complexService;

	@Mock
	private ComplexFilterService complexFilterService;

	@Mock
	private LikeQueryUseCase likeService;

	@Mock
	private FacilityUseCase facilityService;

	@Mock
	private PinPointUseCase pinPointService;

	@Mock
	private DistanceCacheService distanceCacheService;

	@InjectMocks
	private NoticeService noticeService;

	@Test
	void compareUnitTypes_sortsDepositsGloballyAcrossComplexes() {
		String noticeId = "notice-1";
		ComplexDocument complexA = complex("complex-a", "A단지", "서울시 강남구", List.of(
			unitType("type-1", "30A", 30.0, 1_000_000L),
			unitType("type-2", "40A", 40.0, 3_000_000L)
		));
		ComplexDocument complexB = complex("complex-b", "B단지", "서울시 관악구", List.of(
			unitType("type-3", "20A", 20.0, 2_000_000L),
			unitType("type-4", "25A", 25.0, 2_500_000L)
		));

		when(repository.findById(noticeId)).thenReturn(Optional.of(mock(NoticeDocument.class)));
		when(complexService.loadSortedComplexes(noticeId, UnitTypeSortType.DEPOSIT_ASC))
			.thenReturn(List.of(complexA, complexB));
		when(facilityService.getNearFacilities(anyString())).thenReturn(null);

		UnitTypeCompareResponse response =
			noticeService.compareUnitTypes(noticeId, null, UnitTypeSortType.DEPOSIT_ASC, null, null);

		assertThat(response.unitTypes())
			.extracting(UnitTypeCompareResponse.UnitTypeComparisonItem::typeId)
			.containsExactly("type-1", "type-3", "type-4", "type-2");
	}

	@Test
	void compareUnitTypes_sortsAreasGloballyAcrossComplexes() {
		String noticeId = "notice-2";
		ComplexDocument complexA = complex("complex-a", "A단지", "서울시 강남구", List.of(
			unitType("type-1", "40A", 40.0, 4_000_000L),
			unitType("type-2", "20A", 20.0, 1_000_000L)
		));
		ComplexDocument complexB = complex("complex-b", "B단지", "서울시 관악구", List.of(
			unitType("type-3", "35A", 35.0, 3_000_000L),
			unitType("type-4", "30A", 30.0, 2_000_000L)
		));

		when(repository.findById(noticeId)).thenReturn(Optional.of(mock(NoticeDocument.class)));
		when(complexService.loadSortedComplexes(noticeId, UnitTypeSortType.AREA_DESC))
			.thenReturn(List.of(complexA, complexB));
		when(facilityService.getNearFacilities(anyString())).thenReturn(null);

		UnitTypeCompareResponse response =
			noticeService.compareUnitTypes(noticeId, null, UnitTypeSortType.AREA_DESC, null, null);

		assertThat(response.unitTypes())
			.extracting(UnitTypeCompareResponse.UnitTypeComparisonItem::typeId)
			.containsExactly("type-1", "type-3", "type-4", "type-2");
	}

	private ComplexDocument complex(String id, String name, String address, List<UnitType> unitTypes) {
		ComplexDocument complex = mock(ComplexDocument.class);
		when(complex.getId()).thenReturn(id);
		when(complex.getName()).thenReturn(name);
		when(complex.getAddress()).thenReturn(Address.builder().full(address).build());
		when(complex.getUnitTypes()).thenReturn(unitTypes);
		return complex;
	}

	private UnitType unitType(String typeId, String typeCode, double areaM2, long depositTotal) {
		return UnitType.builder()
			.typeId(typeId)
			.typeCode(typeCode)
			.exclusiveAreaM2(areaM2)
			.deposit(Deposit.builder().total(depositTotal).build())
			.group(List.of())
			.build();
	}
}
