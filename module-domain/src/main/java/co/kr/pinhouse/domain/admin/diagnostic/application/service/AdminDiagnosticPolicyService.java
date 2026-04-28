package co.kr.pinhouse.domain.admin.diagnostic.application.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse.AgePolicyResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse.AgeRangeGuideResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse.AssetPolicyResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse.IncomeMatrixCellResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse.IncomeMatrixResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse.IncomeMatrixRowResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse.MatrixHeader;
import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse.RentalAssetLimitResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse.RentalIncomePolicyResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.dto.response.AdminDiagnosticPolicyResponse.SupplyIncomeRatioResponse;
import co.kr.pinhouse.domain.admin.diagnostic.application.usecase.AdminDiagnosticPolicyUseCase;
import co.kr.pinhouse.domain.diagnostic.rule.application.usecase.PolicyUseCase;
import co.kr.pinhouse.domain.diagnostic.rule.domain.entity.SupplyType;
import co.kr.pinhouse.domain.housing.notice.domain.entity.NoticeType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDiagnosticPolicyService implements AdminDiagnosticPolicyUseCase {

	private final PolicyUseCase policyUseCase;

	/// 임대 유형 표시 순서
	private static final List<NoticeType> RENTAL_ORDER = List.of(
		NoticeType.PUBLIC_INTEGRATED,
		NoticeType.PERMANENT_RENTAL,
		NoticeType.NATIONAL_RENTAL,
		NoticeType.LONG_TERM_JEONSE,
		NoticeType.PUBLIC_RENTAL,
		NoticeType.HAPPY_HOUSING
	);

	/// 임대 유형별 공급 유형 매핑 (표시 순서 유지)
	private static final Map<NoticeType, List<SupplyType>> RENTAL_SUPPLY_MAP;

	/// 임대 유형별 소득 기준 기준점
	private static final Map<NoticeType, String> INCOME_STANDARD_MAP = Map.of(
		NoticeType.PUBLIC_INTEGRATED, "기준중위소득",
		NoticeType.PERMANENT_RENTAL, "전년도 도시근로자 월평균소득",
		NoticeType.NATIONAL_RENTAL, "전년도 도시근로자 월평균소득",
		NoticeType.LONG_TERM_JEONSE, "전년도 도시근로자 월평균소득",
		NoticeType.PUBLIC_RENTAL, "기준중위소득",
		NoticeType.HAPPY_HOUSING, "기준중위소득"
	);

	/// 임대 유형별 자산 기준 유형
	private static final Map<NoticeType, String> ASSET_TYPE_MAP = Map.of(
		NoticeType.PUBLIC_INTEGRATED, "총자산",
		NoticeType.PERMANENT_RENTAL, "총자산",
		NoticeType.NATIONAL_RENTAL, "총자산",
		NoticeType.LONG_TERM_JEONSE, "부동산자산",
		NoticeType.PUBLIC_RENTAL, "부동산자산",
		NoticeType.HAPPY_HOUSING, "총자산"
	);

	static {
		Map<NoticeType, List<SupplyType>> map = new LinkedHashMap<>();
		map.put(NoticeType.PUBLIC_INTEGRATED, List.of(
			SupplyType.YOUTH_SPECIAL, SupplyType.ELDER_SPECIAL, SupplyType.NEWCOUPLE_SPECIAL,
			SupplyType.MULTICHILD_SPECIAL, SupplyType.SPECIAL, SupplyType.SINGLE_PARENT_SPECIAL,
			SupplyType.MINOR_SPECIAL, SupplyType.FIRST_SPECIAL, SupplyType.ELDER_SUPPORT_SPECIAL,
			SupplyType.GENERAL
		));
		map.put(NoticeType.PERMANENT_RENTAL, List.of(
			SupplyType.GENERAL, SupplyType.NATIONAL_MERIT, SupplyType.NORTH_DEFECTOR, SupplyType.DISABLED
		));
		map.put(NoticeType.NATIONAL_RENTAL, List.of(
			SupplyType.GENERAL, SupplyType.MULTICHILD_SPECIAL, SupplyType.NEWCOUPLE_SPECIAL, SupplyType.NATIONAL_MERIT
		));
		map.put(NoticeType.LONG_TERM_JEONSE, List.of(
			SupplyType.GENERAL, SupplyType.MULTICHILD_SPECIAL, SupplyType.NEWCOUPLE_SPECIAL, SupplyType.NATIONAL_MERIT
		));
		map.put(NoticeType.PUBLIC_RENTAL, List.of(
			SupplyType.STUDENT_SPECIAL, SupplyType.YOUTH_SPECIAL, SupplyType.ELDER_SPECIAL,
			SupplyType.ELDER_SUPPORT_SPECIAL, SupplyType.MULTICHILD_SPECIAL,
			SupplyType.NEWCOUPLE_SPECIAL, SupplyType.FIRST_SPECIAL, SupplyType.GENERAL
		));
		map.put(NoticeType.HAPPY_HOUSING, List.of(
			SupplyType.STUDENT_SPECIAL, SupplyType.YOUTH_SPECIAL, SupplyType.ELDER_SPECIAL,
			SupplyType.NEWCOUPLE_SPECIAL, SupplyType.GENERAL
		));
		RENTAL_SUPPLY_MAP = map;
	}

	@Override
	public AdminDiagnosticPolicyResponse getPolicy() {
		return new AdminDiagnosticPolicyResponse(
			buildAgePolicy(),
			buildAssetPolicy(),
			buildIncomePolicy(),
			buildIncomeMatrix()
		);
	}

	private AgePolicyResponse buildAgePolicy() {
		int elderAge = policyUseCase.elderAge();
		int youthMin = policyUseCase.youthAgeMin();
		int minorMax = policyUseCase.marriedYouthAgeMin() - 1;
		int newlyMarriedMax = policyUseCase.newlyMarriedMaxYears();

		List<AgeRangeGuideResponse> guides = List.of(
			new AgeRangeGuideResponse(
				minorMax + "세 이하",
				null, minorMax,
				List.of(SupplyType.SPECIAL.name()),
				List.of(SupplyType.SPECIAL.getValue())
			),
			new AgeRangeGuideResponse(
				youthMin + "~39세",
				youthMin, 39,
				List.of(SupplyType.YOUTH_SPECIAL.name(), SupplyType.NEWCOUPLE_SPECIAL.name(),
					SupplyType.FIRST_SPECIAL.name(), SupplyType.MULTICHILD_SPECIAL.name(),
					SupplyType.SINGLE_PARENT_SPECIAL.name(), SupplyType.MINOR_SPECIAL.name(),
					SupplyType.GENERAL.name()),
				List.of(SupplyType.YOUTH_SPECIAL.getValue(), SupplyType.NEWCOUPLE_SPECIAL.getValue(),
					SupplyType.FIRST_SPECIAL.getValue(), SupplyType.MULTICHILD_SPECIAL.getValue(),
					SupplyType.SINGLE_PARENT_SPECIAL.getValue(), SupplyType.MINOR_SPECIAL.getValue(),
					SupplyType.GENERAL.getValue())
			),
			new AgeRangeGuideResponse(
				"40~" + (elderAge - 1) + "세",
				40, elderAge - 1,
				List.of(SupplyType.NEWCOUPLE_SPECIAL.name(), SupplyType.FIRST_SPECIAL.name(),
					SupplyType.MULTICHILD_SPECIAL.name(), SupplyType.SINGLE_PARENT_SPECIAL.name(),
					SupplyType.MINOR_SPECIAL.name(), SupplyType.ELDER_SUPPORT_SPECIAL.name(),
					SupplyType.GENERAL.name()),
				List.of(SupplyType.NEWCOUPLE_SPECIAL.getValue(), SupplyType.FIRST_SPECIAL.getValue(),
					SupplyType.MULTICHILD_SPECIAL.getValue(), SupplyType.SINGLE_PARENT_SPECIAL.getValue(),
					SupplyType.MINOR_SPECIAL.getValue(), SupplyType.ELDER_SUPPORT_SPECIAL.getValue(),
					SupplyType.GENERAL.getValue())
			),
			new AgeRangeGuideResponse(
				elderAge + "세 이상",
				elderAge, null,
				List.of(SupplyType.ELDER_SPECIAL.name(), SupplyType.ELDER_SUPPORT_SPECIAL.name(),
					SupplyType.NEWCOUPLE_SPECIAL.name(), SupplyType.FIRST_SPECIAL.name(),
					SupplyType.MULTICHILD_SPECIAL.name(), SupplyType.GENERAL.name()),
				List.of(SupplyType.ELDER_SPECIAL.getValue(), SupplyType.ELDER_SUPPORT_SPECIAL.getValue(),
					SupplyType.NEWCOUPLE_SPECIAL.getValue(), SupplyType.FIRST_SPECIAL.getValue(),
					SupplyType.MULTICHILD_SPECIAL.getValue(), SupplyType.GENERAL.getValue())
			)
		);

		return new AgePolicyResponse(elderAge, youthMin, 39, minorMax, newlyMarriedMax, guides);
	}

	private AssetPolicyResponse buildAssetPolicy() {
		long carMax = policyUseCase.checkMaxCarValue();

		List<RentalAssetLimitResponse> limits = RENTAL_ORDER.stream()
			.map(rental -> {
				long won = policyUseCase.maxTotalAsset(SupplyType.GENERAL, rental, 1);
				return new RentalAssetLimitResponse(
					rental.name(),
					rental.getValue(),
					ASSET_TYPE_MAP.getOrDefault(rental, "총자산"),
					won,
					formatWon(won)
				);
			})
			.toList();

		return new AssetPolicyResponse(carMax, formatWon(carMax), limits);
	}

	private List<RentalIncomePolicyResponse> buildIncomePolicy() {
		return RENTAL_ORDER.stream()
			.map(rental -> {
				List<SupplyType> supplies = RENTAL_SUPPLY_MAP.getOrDefault(rental, List.of());
				List<SupplyIncomeRatioResponse> ratios = supplies.stream()
					.map(supply -> new SupplyIncomeRatioResponse(
						supply.name(),
						supply.getValue(),
						policyUseCase.maxIncomeRatio(supply, rental, 1),
						policyUseCase.maxIncomeRatio(supply, rental, 2),
						policyUseCase.maxIncomeRatio(supply, rental, 3)
					))
					.toList();

				return new RentalIncomePolicyResponse(
					rental.name(),
					rental.getValue(),
					INCOME_STANDARD_MAP.getOrDefault(rental, "기준중위소득"),
					ratios
				);
			})
			.toList();
	}

	private IncomeMatrixResponse buildIncomeMatrix() {
		/// 매트릭스에 포함할 공급 유형 (전체 임대 유형에 걸쳐 등장하는 것들의 합집합, 표시 순서 유지)
		List<SupplyType> allSupplyTypes = new ArrayList<>();
		Set<SupplyType> seen = new java.util.LinkedHashSet<>();
		RENTAL_ORDER.forEach(rental ->
				RENTAL_SUPPLY_MAP.getOrDefault(rental, List.of()).forEach(s -> {
					if (seen.add(s)) {
						allSupplyTypes.add(s);
					}
				})
		);

		/// 열 헤더
		List<MatrixHeader> rentalHeaders = RENTAL_ORDER.stream()
			.map(r -> new MatrixHeader(r.name(), r.getValue()))
			.toList();

		/// 행 구성
		List<IncomeMatrixRowResponse> rows = allSupplyTypes.stream()
			.map(supply -> {
				List<IncomeMatrixCellResponse> cells = RENTAL_ORDER.stream()
					.map(rental -> {
						boolean applicable = RENTAL_SUPPLY_MAP
							.getOrDefault(rental, List.of())
							.contains(supply);

						if (!applicable) {
							return new IncomeMatrixCellResponse(rental.name(), false, null, -1, -1, -1);
						}

						return new IncomeMatrixCellResponse(
							rental.name(),
							true,
							INCOME_STANDARD_MAP.getOrDefault(rental, "기준중위소득"),
							policyUseCase.maxIncomeRatio(supply, rental, 1),
							policyUseCase.maxIncomeRatio(supply, rental, 2),
							policyUseCase.maxIncomeRatio(supply, rental, 3)
						);
					})
					.toList();

				return new IncomeMatrixRowResponse(supply.name(), supply.getValue(), cells);
			})
			.toList();

		return new IncomeMatrixResponse(rentalHeaders, rows);
	}

	private String formatWon(long won) {
		long uk = won / 100_000_000L;
		long man = (won % 100_000_000L) / 10_000L;

		if (uk > 0 && man > 0) {
			return uk + "억 " + String.format("%,d", man) + "만원";
		} else if (uk > 0) {
			return uk + "억원";
		} else {
			return String.format("%,d", man) + "만원";
		}
	}
}
