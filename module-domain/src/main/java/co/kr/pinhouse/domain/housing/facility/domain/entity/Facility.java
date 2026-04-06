package co.kr.pinhouse.domain.housing.facility.domain.entity;

import co.kr.pinhouse.domain.Location;

public interface Facility {

	/**
	 * 시설의 위치 정보를 반환합니다.
	 * @return 시설 위치 객체
	 */
	Location getLocation();

}
