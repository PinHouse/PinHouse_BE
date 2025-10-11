package com.pinHouse.server.platform.housing.notice.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Urls {

    @Field("apply")
    private String apply;

    @Field("myhomePc")
    private String myhomePc;

    @Field("myhomeMo")
    private String myhomeMo;

    /// 빌더 생성자
    @Builder
    public Urls(String apply, String myhomePc, String myhomeMo) {
        this.apply = apply;
        this.myhomePc = myhomePc;
        this.myhomeMo = myhomeMo;
    }
}
