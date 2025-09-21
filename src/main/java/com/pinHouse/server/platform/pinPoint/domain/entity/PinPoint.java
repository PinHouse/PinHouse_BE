package com.pinHouse.server.platform.pinPoint.domain.entity;

import com.pinHouse.server.platform.BaseTimeEntity;
import com.pinHouse.server.platform.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PinPoint extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /// 주소
    private String address;

    /// 설정한 이름
    private String name;

    /// 좌표
    private double latitude;
    private double longitude;

    private boolean isFirst;

    /// 생성자
    public static PinPoint of (User user, String address, String name, double latitude, double longitude, boolean isFirst) {
        return PinPoint.builder()
                .user(user)
                .address(address)
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .isFirst(isFirst)
                .build();
    }

}
