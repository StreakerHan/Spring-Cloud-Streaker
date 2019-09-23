package com.streaker.common.service;

import com.streaker.common.dto.LogDO;
import com.streaker.common.intercepter.FeignIntercepter;
import com.streaker.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;

import feign.Headers;

@Headers("Content-Type:application/json")
@FeignClient(name = "api-cms", configuration = FeignIntercepter.class)
public interface LogRpcService {
    @Async
    @RequestMapping("/api/log/insert.do")
    R save(LogDO logDO);
}
