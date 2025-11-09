package com.pinHouse.server.platform.pinPoint.domain.entity;

import com.pinHouse.server.platform.Location;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection = "pinpoint")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PinPoint {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    /// 주소
    @Column(nullable = false)
    private String address;

    /// 설정한 이름
    @Column(nullable = false)
    private String name;

    /// 좌표
    @Field("location")
    private Location location;

    @Column(nullable = false)
    private boolean isFirst;

    /// 빌더 생성자
    @Builder
    protected PinPoint(String userId, String address, String name, double latitude, double longitude, boolean isFirst) {
        this.id = UUID.randomUUID().toString(); /// 랜덤
        this.userId = userId;
        this.address = address;
        this.name = name;
        this.location = Location.of(longitude, latitude);
        this.isFirst = isFirst;
    }

    /// 생성자
    public static PinPoint of (String userId, String address, String name, double latitude, double longitude, boolean isFirst) {
        return PinPoint.builder()
                .userId(userId)
                .address(address)
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .isFirst(isFirst)
                .build();
    }

    /// 비즈니스 로직
    public void updateName(String newName) {

        if (newName != null && !newName.isEmpty()) {
            this.name = newName;
        }


    }
}
