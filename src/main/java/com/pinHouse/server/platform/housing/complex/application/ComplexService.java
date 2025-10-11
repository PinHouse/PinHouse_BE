package com.pinHouse.server.platform.housing.complex.application;

import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.housing.complex.application.dto.DistanceResponse;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.housing.complex.domain.repository.ComplexDocumentRepository;
import com.pinHouse.server.platform.housing.complex.application.dto.DepositResponse;
import com.pinHouse.server.platform.pinPoint.application.usecase.PinPointUseCase;
import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ComplexService implements ComplexUseCase{

    /// 의존성
    private final ComplexDocumentRepository repository;
    private final PinPointUseCase pinPointService;

    /// 거리 계산 툴
    private final DistanceUtil distanceUtil;

    // =================
    //  퍼블릭 로직
    // =================

    /**
     * 임대보증금과 월임대료 전환 옵션 계산 메서드
     *
     * @param complexId  공고 ID
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
    @Transactional(readOnly = true)
    public DepositResponse getLeaseByPercent(String complexId, String type, double percentage) {

        // 1) 단지/타입 로드
        ComplexDocument complex = loadComplex(complexId);
        UnitType unitType = complex.getUnitTypes().stream()
                .filter(info -> info.getTypeCode().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 유형(" + type + ")의 공급 정보가 없습니다."));

        long contract = unitType.getDeposit().getContract(); // 계약금
        long balance  = unitType.getDeposit().getBalance();  // 잔금
        long monthRent = unitType.getMonthlyRent();          // 월임대료
        long totalDeposit = Math.max(0, contract + balance); // 보증금 총액 안전화

        // 2) 법정 전환율 계산
        //    - 연 전환율 = min(0.10, 기준금리 + 0.02)
        //    - 기준금리는 외부에서 주입/설정으로 가져오는 것을 권장(아래는 예시 상수)
        double baseRate = 0.025; // 2.5% (예: 2025-05-29 당시 기준금리 2.50%)  ← 환경설정/프로바이더로 교체 권장
        double legalAnnualRate = Math.min(0.10, baseRate + 0.02); // 법정 연 전환율
        double monthlyRate = legalAnnualRate / 12.0;

        // 3) 사용자 입력 비율 클램핑(-1.0 ~ 1.0)
        double p = Math.max(-1.0, Math.min(1.0, percentage));

        // 4) 전환 한도 정의
        //    - 보증금→월세: 최대 전환 가능 보증금 = 현재 총보증금
        //    - 월세→보증금: 최대 전환 가능 월세 = 현재 월세
        //    - 전환 크기는 |p| 비율로 설정
        long targetDepositChange; // (+면 보증금 증가, -면 감소)
        long targetRentChange;    // (+면 월세 증가,   -면 감소)

        if (p > 0) {
            // 보증금 → 월세 (보증금 감소, 월세 증가)
            long depositReduce = Math.round(totalDeposit * p);

            // 10만원 단위 반올림
            depositReduce = Math.round(depositReduce / 100_000.0) * 100_000;

            // 한도: 보증금이 음수로 내려가면 안 됨
            depositReduce = Math.min(depositReduce, totalDeposit);

            long rentIncrease = Math.round(depositReduce * monthlyRate);

            // 1천원 단위 반올림
            rentIncrease = Math.round(rentIncrease / 1_000.0) * 1_000;

            targetDepositChange = -depositReduce;
            targetRentChange = +rentIncrease;

        } else if (p < 0) {
            // 월세 → 보증금 (월세 감소, 보증금 증가)
            long rentReduce = Math.round(monthRent * (-p));

            // 1천원 단위 반올림 및 한도
            rentReduce = Math.round(rentReduce / 1_000.0) * 1_000;
            rentReduce = Math.min(rentReduce, monthRent);

            // 법정 전환식: 증가보증금 = 감소월세 × (12 / 연전환율)
            long depositIncrease = Math.round(rentReduce * (12.0 / legalAnnualRate));

            // 10만원 단위 반올림
            depositIncrease = Math.round(depositIncrease / 100_000.0) * 100_000;

            targetDepositChange = +depositIncrease;
            targetRentChange = -rentReduce;

        } else {
            // p == 0 : 변동 없음
            targetDepositChange = 0;
            targetRentChange = 0;
        }

        // 5) 결과 적용 (balance는 유지, contract로 총액 맞추기)
        long newTotalDeposit = Math.max(0, totalDeposit + targetDepositChange);
        long newMonthRent    = Math.max(0, monthRent + targetRentChange);

        long newBalance  = balance; // 정책상 그대로 유지
        long newContract = Math.max(0, newTotalDeposit - newBalance);

        return DepositResponse.from(
                complex.getComplexKey(),
                type,
                newContract,
                newBalance,
                newMonthRent
        );
    }

    /// 대중교통 시뮬레이터
    @Override
    @Transactional()
    public List<DistanceResponse> getDistance(String id, Long pinPointId) throws UnsupportedEncodingException {

        /// 임대주택 예외처리
        ComplexDocument complex = loadComplex(id);
        Location location = complex.getLocation();

        /// 핀포인트 조회
        PinPoint pinPoint = pinPointService.loadPinPoint(pinPointId);

        /// 2개의 좌표 비교하기
        List<DistanceResponse> path = distanceUtil.findPath(pinPoint.getLatitude(), pinPoint.getLongitude(), location.getLatitude(), location.getLongitude());

        /// 결과 중 처음 내용 리턴
        return path;

    }


    /**
     * 연 3.5% 이자율 기준 월 전환
     */
    private double percentageRateFactor() {
        return 0.035 / 12;
    }

    // =================
    //  외부 로직
    // =================

    /// 상세 조회
    @Override
    @Transactional(readOnly = true)
    public ComplexDocument loadComplex(String id) {
        return repository.findByComplexKey(id)
                .orElseThrow(() -> new NoSuchElementException("Complex Not Found"));
    }

    /// 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<ComplexDocument> loadComplexes() {
        return repository.findAll();
    }

    /// 공고 기반 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<ComplexDocument> loadComplexes(String noticeId) {
        return repository.findByNoticeId(noticeId);
    }
}
