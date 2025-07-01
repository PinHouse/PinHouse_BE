package com.myhome.server.platform.adapter.in.web;

import com.myhome.server.platform.application.out.notice.NoticePort;
import com.myhome.server.platform.domain.notice.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeApi {

    private final NoticePort noticePort;

    @GetMapping
    public List<Notice> getNotices() {

        return noticePort.loadAllNotices();
    }

}
