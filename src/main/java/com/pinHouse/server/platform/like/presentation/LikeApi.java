package com.pinHouse.server.platform.like.presentation;

import com.pinHouse.server.platform.like.application.usecase.LikeUseCase;
import com.pinHouse.server.platform.like.presentation.swagger.LikeApiSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/likes")
@RequiredArgsConstructor
public class LikeApi implements LikeApiSpec {

    private final LikeUseCase service;


}
