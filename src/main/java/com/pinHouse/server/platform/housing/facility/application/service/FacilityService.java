package com.pinHouse.server.platform.housing.facility.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.housing.facility.application.dto.request.FacilityType;
import com.pinHouse.server.platform.housing.facility.application.usecase.FacilityUseCase;
import com.pinHouse.server.platform.housing.facility.domain.entity.*;
import com.pinHouse.server.platform.housing.facility.domain.entity.infra.Facility;
import com.pinHouse.server.platform.housing.facility.domain.repository.*;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.Notice;
import com.pinHouse.server.platform.housing.facility.application.dto.response.FacilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class FacilityService implements FacilityUseCase {

    /// 공고 의존성
    private final NoticeUseCase noticeService;

    /// 인프라 의존성
    private final LibraryDocumentRepository libraryRepository;
    private final AnimalDocumentRepository animalRepository;
    private final ParkDocumentRepository parkRepository;
    private final WalkingDocumentRepository walkingRepository;
    private final SportDocumentRepository sportRepository;

    /// 추가된 부분
    private final ExhibitionDocumentRepository exhibitionRepository;
    private final LaundryDocumentRepository laundryRepository;
    private final HospitalDocumentRepository hospitalRepository;
    private final MartDocumentRepository martRepository;

    /// 상수
    private final double radiusKm = 2;
    private final double radiusInRadians = radiusKm / 6371.0;

    // =================
    //  주변 인프라 조회
    // =================

    /// 주변의 인프라 개수 조회
    @Override
    public FacilityResponse getNoticeInfraById(String noticeId) {

        /// 예외 처리
        Notice notice = getNotice(noticeId);

        /// 주변에 존재하는 도서관 가져오기
        List<Library> libraries = libraryRepository.findByLocation(
                notice.getLocation().getLongitude(),
                notice.getLocation().getLatitude(),
                radiusInRadians);

        /// 주변에 존재하는 동물 관련 시설 가져오기
        List<Animal> animals = animalRepository.findByLocation(
                notice.getLocation().getLongitude(),
                notice.getLocation().getLatitude(),
                radiusInRadians);


        /// 주변에 존재하는 공원 정보 시설 가져오기
        List<Park> parks = parkRepository.findByLocation(
                notice.getLocation().getLongitude(),
                notice.getLocation().getLatitude(),
                radiusInRadians);


        /// 주변에 존재하는 산책로 관련 시설 가져오기
        List<Walking> walkings = walkingRepository.findByLocation(
                notice.getLocation().getLongitude(),
                notice.getLocation().getLatitude(),
                radiusInRadians);


        /// 주변에 존재하는 스포츠 정보 시설 가져오기
        List<Sport> sports = sportRepository.findByLocation(
                notice.getLocation().getLongitude(),
                notice.getLocation().getLatitude(),
                radiusInRadians);

        /// 주변에 존재하는 전시회
        List<Exhibition> exhibitions = exhibitionRepository.findByLocation(
                notice.getLocation().getLongitude(),
                notice.getLocation().getLatitude(),
                radiusInRadians
        );

        /// 주변에 존재하는 빨래
        List<Laundry> laundries = laundryRepository.findByLocation(
                notice.getLocation().getLongitude(),
                notice.getLocation().getLatitude(),
                radiusInRadians
        );

        /// 주변에 존재하는 병원
        List<Hospital> hospitals = hospitalRepository.findByLocation(
                notice.getLocation().getLongitude(),
                notice.getLocation().getLatitude(),
                radiusInRadians
        );

        /// 주변에 존재하는 대형마트
        List<Mart> marts = martRepository.findByLocation(
                notice.getLocation().getLongitude(),
                notice.getLocation().getLatitude(),
                radiusInRadians
        );
        /// 객체 생성
        return FacilityResponse.of(notice, libraries, animals, sports, walkings, parks, exhibitions, laundries, hospitals, marts);
    }

    // =================
    //  인프라 바탕으로 공고 조회
    // =================
    @Override
    public List<Notice> getNoticesByInfraTypesWithAllMinCount(List<FacilityType> facilityTypes) {
        List<Notice> allNotices = noticeService.loadAllNotices();

        return allNotices.stream()
                .filter(notice -> {
                    double lng = notice.getLocation().getLongitude();
                    double lat = notice.getLocation().getLatitude();

                    // 모든 종류가 2개 이상이어야만 true 반환
                    return facilityTypes.stream().allMatch(facilityType -> {
                        List<? extends Facility> facilityList = switch (facilityType) {
                            case LIBRARY -> libraryRepository.findByLocation(lng, lat, radiusInRadians);
                            case PARK -> parkRepository.findByLocation(lng, lat, radiusInRadians);
                            case ANIMAL -> animalRepository.findByLocation(lng, lat, radiusInRadians);
                            case WALKING -> walkingRepository.findByLocation(lng, lat, radiusInRadians);
                            case SPORT -> sportRepository.findByLocation(lng, lat, radiusInRadians);
                            case EXHIBITION -> exhibitionRepository.findByLocation(lng, lat, radiusInRadians);
                            case LAUNDRY -> laundryRepository.findByLocation(lng, lat, radiusInRadians);
                            case HOSPITAL -> hospitalRepository.findByLocation(lng, lat, radiusInRadians);
                            case STORE -> martRepository.findByLocation(lng, lat, radiusInRadians);
                        };
                        return facilityList.size() >= 2;
                    });
                })
                .toList();
    }


    // =================
    //  내부 함수
    // =================

    private Notice getNotice(String noticeId) {
        return noticeService.loadById(noticeId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.NOT_NOTICE.getMessage()));
    }

}
