package co.kr.pinhouse.domain.housing.facility.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.domain.housing.complex.domain.entity.ComplexDocument;
import co.kr.pinhouse.domain.housing.complex.domain.repository.ComplexDocumentRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FacilityBatch {

	private final FacilityStatService statService;
	private final ComplexDocumentRepository complexRepository;

	/// 다 저장하는 함수
	public void getFacilities() {

		/// 모든 아이디 가져오기
		List<ComplexDocument> documents = complexRepository.findAll();

		documents.forEach(document -> {

			/// 좌표
			Double longitude = document.getLocation().getLongitude();
			Double latitude = document.getLocation().getLatitude();

			/// 인프라 개수 가져오기
			statService.getCountsOrRecompute(document.getId(), longitude, latitude);
		});
	}

}
