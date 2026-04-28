package co.kr.pinhouse.domain.admin.diagnostic.application.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "청약진단 정책 설정값 응답 (어드민 시각화용)")
public record AdminDiagnosticPolicyResponse(
	@Schema(description = "나이 관련 정책 (나이 기준 요약 카드용)")
	AgePolicyResponse agePolicy,
	@Schema(description = "자산 관련 정책 (막대 차트용)")
	AssetPolicyResponse assetPolicy,
	@Schema(description = "임대유형별 소득 기준 (임대유형 탭별 표 렌더링용)")
	List<RentalIncomePolicyResponse> incomePolicy,
	@Schema(description = "소득 비율 교차 매트릭스 (히트맵/피벗 테이블용). 행=공급유형, 열=임대유형")
	IncomeMatrixResponse incomeMatrix
) {

	// ─────────────────── 나이 정책 ───────────────────

	@Schema(description = "나이 기준 정책")
	public record AgePolicyResponse(
		@Schema(description = "고령자 기준 나이 이상이면 고령자 특별공급 대상", example = "65")
		int elderAgeThreshold,
		@Schema(description = "청년 특별공급 최소 나이", example = "19")
		int youthAgeMin,
		@Schema(description = "청년 특별공급 최대 나이", example = "39")
		int youthAgeMax,
		@Schema(description = "미성년자 결혼 특별공급 최대 나이(이 나이 미만)", example = "18")
		int marriedMinorAgeMax,
		@Schema(description = "신혼부부 특별공급 최대 혼인 기간(년)", example = "7")
		int newlyMarriedMaxYears,
		@Schema(description = "나이 구간별 신청 가능 공급유형 가이드 (타임라인 차트용)")
		List<AgeRangeGuideResponse> ageRangeGuides
	) {
	}

	@Schema(description = "나이 구간별 신청 가능 공급유형")
	public record AgeRangeGuideResponse(
		@Schema(description = "나이 구간 레이블", example = "18세 이하")
		String ageRangeLabel,
		@Schema(description = "나이 범위 시작 (inclusive). null이면 하한 없음")
		Integer ageFrom,
		@Schema(description = "나이 범위 끝 (inclusive). null이면 상한 없음")
		Integer ageTo,
		@Schema(description = "이 구간에서 신청 가능한 공급 유형 코드 목록")
		List<String> eligibleSupplyTypeCodes,
		@Schema(description = "이 구간에서 신청 가능한 공급 유형 명칭 목록")
		List<String> eligibleSupplyTypeNames
	) {
	}

	// ─────────────────── 자산 정책 ───────────────────

	@Schema(description = "자산 한도 정책")
	public record AssetPolicyResponse(
		@Schema(description = "자동차 자산 한도 (원)", example = "45420000")
		long maxCarValueWon,
		@Schema(description = "자동차 자산 한도 텍스트", example = "4,542만원")
		String maxCarValueLabel,
		@Schema(description = "임대유형별 자산 한도 목록 (막대 차트 데이터)")
		List<RentalAssetLimitResponse> rentalLimits
	) {
	}

	@Schema(description = "임대유형별 자산 한도 (막대 차트 1개 항목)")
	public record RentalAssetLimitResponse(
		@Schema(description = "임대 유형 코드", example = "PUBLIC_INTEGRATED")
		String rentalType,
		@Schema(description = "임대 유형 명칭", example = "통합공공임대")
		String rentalLabel,
		@Schema(description = "적용 자산 기준", example = "총자산")
		String assetType,
		@Schema(description = "자산 한도 (원) — 차트 수치 값", example = "345000000")
		long limitWon,
		@Schema(description = "자산 한도 텍스트 — 차트 레이블", example = "3억 4,500만원")
		String limitLabel
	) {
	}

	// ─────────────────── 임대유형별 소득 정책 ───────────────────

	@Schema(description = "임대유형별 소득 기준 정책 (탭 전환 표용)")
	public record RentalIncomePolicyResponse(
		@Schema(description = "임대 유형 코드", example = "PUBLIC_INTEGRATED")
		String rentalType,
		@Schema(description = "임대 유형 명칭", example = "통합공공임대")
		String rentalLabel,
		@Schema(description = "소득 기준 기준점", example = "기준중위소득")
		String incomeStandard,
		@Schema(description = "공급유형별 소득 비율 목록 (표의 행)")
		List<SupplyIncomeRatioResponse> supplyRatios
	) {
	}

	@Schema(description = "공급유형별 소득 비율 (표 1행)")
	public record SupplyIncomeRatioResponse(
		@Schema(description = "공급 유형 코드", example = "YOUTH_SPECIAL")
		String supplyType,
		@Schema(description = "공급 유형 명칭", example = "청년 특별공급")
		String supplyLabel,
		@Schema(description = "1인 가구 소득 비율 (%)", example = "170.0")
		double family1Percent,
		@Schema(description = "2인 가구 소득 비율 (%)", example = "160.0")
		double family2Percent,
		@Schema(description = "3인 이상 가구 소득 비율 (%)", example = "150.0")
		double family3PlusPercent
	) {
	}

	// ─────────────────── 소득 교차 매트릭스 ───────────────────

	@Schema(description = "소득 비율 교차 매트릭스 (히트맵/피벗 테이블 렌더링용)")
	public record IncomeMatrixResponse(
		@Schema(description = "열 헤더: 임대 유형 목록 (코드 + 명칭)")
		List<MatrixHeader> rentalTypeHeaders,
		@Schema(description = "행: 공급 유형별로 각 임대유형의 소득 비율 셀 목록")
		List<IncomeMatrixRowResponse> rows
	) {
	}

	@Schema(description = "매트릭스 헤더 항목")
	public record MatrixHeader(
		String code,
		String label
	) {
	}

	@Schema(description = "교차 매트릭스 행 (공급 유형 기준)")
	public record IncomeMatrixRowResponse(
		@Schema(description = "공급 유형 코드")
		String supplyTypeCode,
		@Schema(description = "공급 유형 명칭")
		String supplyTypeLabel,
		@Schema(description = "각 임대유형별 셀. rentalTypeHeaders와 동일 순서로 정렬")
		List<IncomeMatrixCellResponse> cells
	) {
	}

	@Schema(description = "교차 매트릭스 셀")
	public record IncomeMatrixCellResponse(
		@Schema(description = "임대 유형 코드")
		String rentalTypeCode,
		@Schema(description = "해당 조합이 존재하면 true")
		boolean applicable,
		@Schema(description = "소득 기준 기준점. applicable=false면 null")
		String incomeStandard,
		@Schema(description = "1인 가구 소득 비율 (%). applicable=false면 -1")
		double family1Percent,
		@Schema(description = "2인 가구 소득 비율 (%). applicable=false면 -1")
		double family2Percent,
		@Schema(description = "3인 이상 가구 소득 비율 (%). applicable=false면 -1")
		double family3PlusPercent
	) {
	}
}
