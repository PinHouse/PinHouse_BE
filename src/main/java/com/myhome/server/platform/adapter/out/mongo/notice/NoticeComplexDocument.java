package com.myhome.server.platform.adapter.out.mongo.notice;

import com.myhome.server.platform.domain.notice.NoticeComplex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeComplexDocument {

    @Field("단지명")
    private String name;

    @Field("주소")
    private String address;

    @Field("전용면적")
    private String area;

    @Field("총세대수")
    private String totalHouseholds;

    @Field("난방방식")
    private String heatingMethod;

    @Field("입주예정월")
    private String expectedMoveInMonth;

    /// ToDomain
    NoticeComplex toDomain() {
        return NoticeComplex.builder()
                .name(name)
                .address(address)
                .area(area)
                .totalHouseholds(totalHouseholds)
                .heatingMethod(heatingMethod)
                .expectedMoveInMonth(expectedMoveInMonth)
                .build();
    }

}
