package co.kr.pinhouse.domain.diagnostic.rule.domain.entity;

import co.kr.pinhouse.domain.housing.notice.domain.entity.NoticeType;

/**
 * 내부 정책 variant.
 * 응답 DTO에는 노출하지 않고, 후보별 세부 판정과 exact-support 여부만 관리한다.
 */
public enum CandidatePolicyKey {

	DEFAULT(true, null),
	INTEGRATED_YOUTH_18_TO_39(true, null),
	YOUTH_19_TO_39(true, null),
	SINGLE_PARENT_WITH_INFANT_OR_FETUS(true, null),
	NEWLY_MARRIED_GENERIC(true, null),
	UNSUPPORTED_PUBLIC_RENTAL_PRODUCT(
		false, "공공임대는 5년·6년·10년 유형과 모집공고별 면적 기준 정보가 필요합니다."),
	UNSUPPORTED_LONG_TERM_JEONSE_REGION(
		false, "장기전세는 지역별 자산 기준과 모집공고 정보가 필요합니다."),
	UNSUPPORTED_STUDENT_PARENT_INCOME(
		false, "행복주택 대학생·취업준비생은 부모 소득 및 자산 정보가 필요합니다."),
	UNSUPPORTED_MINOR_CHILD_AGE(
		false, "신생아 특별공급은 2세 미만 자녀 여부를 확인할 입력이 필요합니다."),
	UNSUPPORTED_FIRST_HOME_HISTORY(
		false, "생애최초 특별공급은 과거 주택 소유 이력과 소득세 납부 이력이 필요합니다."),
	UNSUPPORTED_ELDER_SUPPORT_DURATION(
		false, "노부모부양 특별공급은 1년 또는 3년 이상 부양기간 정보가 필요합니다.");

	private final boolean exactSupported;
	private final String unsupportedReason;

	CandidatePolicyKey(boolean exactSupported, String unsupportedReason) {
		this.exactSupported = exactSupported;
		this.unsupportedReason = unsupportedReason;
	}

	public static CandidatePolicyKey resolve(NoticeType noticeType, SupplyType supplyType) {
		if (noticeType == NoticeType.PUBLIC_RENTAL) {
			return UNSUPPORTED_PUBLIC_RENTAL_PRODUCT;
		}

		if (noticeType == NoticeType.LONG_TERM_JEONSE) {
			return UNSUPPORTED_LONG_TERM_JEONSE_REGION;
		}

		return switch (supplyType) {
			case STUDENT_SPECIAL -> UNSUPPORTED_STUDENT_PARENT_INCOME;
			case MINOR_SPECIAL -> UNSUPPORTED_MINOR_CHILD_AGE;
			case FIRST_SPECIAL -> UNSUPPORTED_FIRST_HOME_HISTORY;
			case ELDER_SUPPORT_SPECIAL -> UNSUPPORTED_ELDER_SUPPORT_DURATION;
			case YOUTH_SPECIAL -> noticeType == NoticeType.PUBLIC_INTEGRATED
				? INTEGRATED_YOUTH_18_TO_39
				: YOUTH_19_TO_39;
			case SINGLE_PARENT_SPECIAL -> SINGLE_PARENT_WITH_INFANT_OR_FETUS;
			case NEWCOUPLE_SPECIAL -> NEWLY_MARRIED_GENERIC;
			default -> DEFAULT;
		};
	}

	public boolean exactSupported() {
		return exactSupported;
	}

	public String unsupportedReason() {
		return unsupportedReason;
	}
}
