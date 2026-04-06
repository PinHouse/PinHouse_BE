package co.kr.pinhouse.domain.housing.facility.domain.entity.infra;

import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import co.kr.pinhouse.domain.Location;
import co.kr.pinhouse.domain.housing.facility.domain.entity.Facility;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "laundry")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Laundry implements Facility {

	@Id
	private String id; // ObjectId 매핑

	@Field("번호")
	private int number;

	@Field("개방서비스명")
	private String openServiceName;

	@Field("상세영업상태명")
	private String detailedBusinessStatus;

	@Field("소재지면적")
	private double area;

	@Field("소재지전체주소")
	private String fullAddress;

	@Field("도로명전체주소")
	private String roadNameAddress;

	@Field("사업장명")
	private String businessName;

	@Field("업태구분명")
	private String businessType;

	@Field("위생업태명")
	private String sanitationType;

	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
	private Location location;

}
