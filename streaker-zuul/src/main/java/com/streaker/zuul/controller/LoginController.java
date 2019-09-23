package com.streaker.zuul.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.streaker.common.constants.CommonConstants;
import com.streaker.common.context.FilterContextHandler;
import com.streaker.common.dto.MenuDTO;
import com.streaker.zuul.prc.admin.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class LoginController {
    @Autowired
    MenuService menuService;
    @GetMapping({"/test"})
    List<MenuDTO> login(HttpServletRequest request)  {
        FilterContextHandler.setToken(request.getHeader(CommonConstants.CONTEXT_TOKEN));
        return menuService.userMenus();
    }
}
