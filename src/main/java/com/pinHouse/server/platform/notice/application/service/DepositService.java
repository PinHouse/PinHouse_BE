package com.pinHouse.server.platform.notice.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.notice.application.dto.response.NoticeSupplyDTO;
import com.pinHouse.server.platform.notice.application.usecase.DepositUseCase;
import com.pinHouse.server.platform.notice.domain.entity.Notice;
import com.pinHouse.server.platform.notice.domain.entity.NoticeSupply;
import com.pinHouse.server.platform.notice.domain.repository.NoticeDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class DepositService implements DepositUseCase {

    private final NoticeDocumentRepository repository;

    /**
     * 임대보증금과 월임대료 전환 옵션 계산 메서드
     *
     * @param noticeId   공고 ID
     * @param type       공급 유형(예: '전세', '월세')
     * @param percentage 전환 비율(0.0~1.0, 예: 0.5는 50% 전환)
     *
     * <전환 규칙>
     * - 임대보증금 100만원 단위로만 전환 가능
     * - 임대보증금 → 월임대료: 연 3.5% 적용 (월이율 = 3.5%/12)
     * - 월임대료 → 임대보증금: 연 7% 적용 (월이율 = 7%/12)
     * - 전환 이율 변동 시, 변경된 이율로 재산정
     */
    @Override
    public NoticeSupplyDTO.NoticeLeaseOptionResponse getLeaseByPercent(String noticeId, String type, double percentage) {

        // 1. 공고 예외처리
        Notice notice = getNotice(noticeId);

        // 2. 공급 정보 필터링
        NoticeSupply matchedSupplyInfo = notice.getSupplyInfo().stream()
                .filter(info -> info.getHousingType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 유형(" + type + ")의 공급 정보가 없습니다."));

        // 3. 원본 보증금, 월세
        long originalDeposit = matchedSupplyInfo.getDeposit().getTotal();
        long originalRent = matchedSupplyInfo.getMonthlyRent();

        // 4. 최대 전환 가능한 보증금 (예: 10,000,000원으로 조정 가능)
        long maxConvertibleDeposit = 10_000_000L;  // 필요에 따라 조정 가능

        // 5. 동적 최대 전환 비율 계산
        double maxDepositRate = (double) maxConvertibleDeposit / originalDeposit;

        // 6. 월세 전환율은 보증금 전환율 대비 비율 유지 (3.5% / 7%)
        double baseDepositRate = 0.07;   // 7%
        double baseRentRate = 0.035;     // 3.5%
        double maxRentRate = maxDepositRate * (baseRentRate / baseDepositRate);

        // 7. 월 이율 계산 (고정)
        double monthlyDepositToRentRate = baseRentRate / 12;  // 보증금 → 월세 월 이율
        double monthlyRentToDepositRate = baseDepositRate / 12; // 월세 → 보증금 월 이율

        // 8. 사용자 입력 비율 클램핑 (-1.0 ~ 1.0)
        double clampedPercentage = Math.max(-1.0, Math.min(1.0, percentage));

        long adjustedDeposit = originalDeposit;
        long adjustedRent = originalRent;

        if (clampedPercentage > 0) {
            // 보증금 → 월세 전환
            long convertibleDeposit = Math.round(originalDeposit * maxDepositRate * clampedPercentage);
            // 10만원 단위 반올림
            convertibleDeposit = Math.round(convertibleDeposit / 100_000.0) * 100_000;

            long additionalRent = Math.round(convertibleDeposit * monthlyDepositToRentRate);

            adjustedDeposit = originalDeposit - convertibleDeposit;
            adjustedRent = originalRent + additionalRent;

        } else if (clampedPercentage < 0) {
            // 월세 → 보증금 전환
            long convertibleRent = Math.round(originalRent * maxRentRate * (-clampedPercentage));

            long increasedDeposit = Math.round(convertibleRent / monthlyRentToDepositRate);
            // 10만원 단위 반올림
            increasedDeposit = Math.round(increasedDeposit / 100_000.0) * 100_000;

            adjustedDeposit = originalDeposit + increasedDeposit;
            adjustedRent = originalRent - convertibleRent;
        }
        // clampedPercentage == 0 이면 원본 값 그대로 유지

        return NoticeSupplyDTO.NoticeLeaseOptionResponse
                .from(notice.getNoticeId(), type, adjustedDeposit, adjustedRent);
    }

    /**
     * 연 3.5% 이자율 기준 월 전환
     */
    private double percentageRateFactor() {
        return 0.035 / 12;
    }


    /**
     * - ID를 바탕으로 공고를 조회하는 함수입니다.
     * @param noticeId  공고 ID
     * @return          Notice 객체
     */
    private Notice getNotice(String noticeId) {
        return repository.findByNoticeId(noticeId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.NOT_NOTICE.getMessage()));
    }

}
