package com.streaker.zuul.prc.admin;

import java.util.List;

import com.streaker.common.dto.MenuDTO;
import com.streaker.common.intercepter.FeignIntercepter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


import feign.Headers;

@Headers("Content-Type:application/json")
@FeignClient(name = "api-cms", configuration = FeignIntercepter.class)
public interface MenuService {
    @GetMapping("/api/menu/userMenus.do")
    List<MenuDTO> userMenus();
}
