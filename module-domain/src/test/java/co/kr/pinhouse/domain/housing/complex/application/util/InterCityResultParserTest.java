package co.kr.pinhouse.domain.housing.complex.application.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.kr.pinhouse.domain.housing.complex.application.dto.response.ChipType;
import co.kr.pinhouse.domain.housing.complex.domain.transit.BusRouteType;
import co.kr.pinhouse.domain.housing.complex.domain.transit.ExpressBusType;
import co.kr.pinhouse.domain.housing.complex.domain.transit.InterCityResult;
import co.kr.pinhouse.domain.housing.complex.domain.transit.LineInfo;
import co.kr.pinhouse.domain.housing.complex.domain.transit.RootResult;
import co.kr.pinhouse.domain.housing.complex.domain.transit.SubwayLineType;

class InterCityResultParserTest {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final TransitResponseMapper mapper = new TransitResponseMapper();

	@Test
	void parse_mixedInterCityRoute_keepsTransportMetadataForSegmentColors() throws Exception {
		String json = "{"
			+ "\"result\":{"
			+ "\"searchType\":1,"
			+ "\"busCount\":1,"
			+ "\"trainCount\":0,"
			+ "\"airCount\":0,"
			+ "\"mixedCount\":1,"
			+ "\"path\":[{"
			+ "\"info\":{"
			+ "\"totalTime\":82,"
			+ "\"totalPayment\":4500,"
			+ "\"totalDistance\":42000"
			+ "},"
			+ "\"subPath\":["
			+ "{"
			+ "\"trafficType\":1,"
			+ "\"sectionTime\":12,"
			+ "\"distance\":6000,"
			+ "\"startName\":\"서울역\","
			+ "\"endName\":\"고속터미널\","
			+ "\"lane\":[{"
			+ "\"name\":\"1\","
			+ "\"subwayCode\":\"1\""
			+ "}]"
			+ "},"
			+ "{"
			+ "\"trafficType\":2,"
			+ "\"sectionTime\":18,"
			+ "\"distance\":8500,"
			+ "\"startName\":\"고속터미널\","
			+ "\"endName\":\"서울경부\","
			+ "\"lane\":[{"
			+ "\"busNo\":\"9401\","
			+ "\"type\":\"14\""
			+ "}]"
			+ "},"
			+ "{"
			+ "\"trafficType\":5,"
			+ "\"sectionTime\":52,"
			+ "\"distance\":27500,"
			+ "\"startName\":\"서울경부\","
			+ "\"endName\":\"천안종합터미널\","
			+ "\"lane\":[{"
			+ "\"type\":\"2\""
			+ "}]"
			+ "}"
			+ "]"
			+ "}]"
			+ "}"
			+ "}";

		InterCityResult result = InterCityResultParser.parse(OBJECT_MAPPER.readTree(json));

		assertThat(result.routes()).hasSize(1);
		RootResult route = result.routes().getFirst();
		assertThat(route.steps()).hasSize(3);

		RootResult.DistanceStep subwayStep = route.steps().get(0);
		assertThat(subwayStep.subwayLine()).isEqualTo(SubwayLineType.SEOUL_LINE_1);
		assertThat(subwayStep.line().bgColorHex()).isEqualTo("#3356B4");

		RootResult.DistanceStep busStep = route.steps().get(1);
		assertThat(busStep.busRouteType()).isEqualTo(BusRouteType.WIDE_AREA);
		assertThat(busStep.lineInfo()).isEqualTo("9401");

		RootResult.DistanceStep expressBusStep = route.steps().get(2);
		assertThat(expressBusStep.expressBusType()).isEqualTo(ExpressBusType.PREMIUM);
		assertThat(expressBusStep.line().bgColorHex()).isEqualTo("#2E933C");

		var segments = mapper.toSegmentResponses(route);
		assertThat(segments)
			.extracting(segment -> segment.colorHex())
			.containsExactly("#3356B4", "#D82628", "#2E933C");
	}

	@Test
	void extractBgColorHex_prefersLineColorWhenEnumSpecificFieldIsMissing() {
		RootResult.DistanceStep step = RootResult.DistanceStep.builder()
			.type(RootResult.TransportType.BUS)
			.time(10)
			.distance(5000)
			.line(LineInfo.of(999, "테스트", "#123456"))
			.build();

		String color = TransportColorResolver.extractBgColorHex(step, ChipType.BUS);

		assertThat(color).isEqualTo("#123456");
	}
}
