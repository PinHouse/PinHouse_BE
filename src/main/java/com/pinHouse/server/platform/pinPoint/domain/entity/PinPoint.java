package com.pinHouse.server.platform.pinPoint.domain.entity;

import com.pinHouse.server.platform.BaseTimeEntity;
import com.pinHouse.server.platform.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PinPoint extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /// 주소
    @Column(nullable = false)
    private String address;

    /// 설정한 이름
    @Column(nullable = false)
    private String name;

    /// 좌표
    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private boolean isFirst;

    /// 빌더 생성자
    @Builder
    public PinPoint(User user, String address, String name, double latitude, double longitude, boolean isFirst) {
        this.user = user;
        this.address = address;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isFirst = isFirst;
    }

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

    /// 비즈니스 로직
    public void updateName(String newName) {

        if (newName != null && !newName.isEmpty()) {
            this.name = newName;
        }


    }
}
