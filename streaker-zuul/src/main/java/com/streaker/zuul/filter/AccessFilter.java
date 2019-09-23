package com.streaker.zuul.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.streaker.common.constants.CommonConstants;
import com.streaker.common.context.FilterContextHandler;
import com.streaker.common.dto.MenuDTO;
import com.streaker.common.dto.UserToken;
import com.streaker.common.util.JSONUtils;
import com.streaker.common.util.JwtUtils;
import com.streaker.common.util.R;
import com.streaker.zuul.prc.admin.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class AccessFilter extends ZuulFilter {
    @Autowired
    MenuService menuService;


    private String ignorePath = "/api-cms/api/login.do,"
    		+ "/api-cms/api/file/,"
    		+ "/api-cms/api/dict/,"
    		+ "/api-cms/cms/,"
    		+ "/api-cms/api/tMember/getCode.do,"
    		+ "/api-cms/api/tMember/insertMember.do,"
    		+ "/api-cms/api/tCompany/getCode.do,"
    		+ "/api-cms/api/tCompany/insert.do,"
    		+ "/api-cms/api/tCompany/insertCompany.do,"
    		+ "/api-cms/api/tExpert/getCode.do,"
    		+ "/api-cms/api/tExpert/insert.do,"
    		+ "/api-cms/api/tCompany/selectCom.do,"
    		+ "/api-cms/api/tExpert/insertExpert.do";

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10000;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }


    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        final String requestUri = request.getRequestURI();
        if (isStartWith(requestUri)) {
            return null;
        }
        String accessToken = request.getHeader(CommonConstants.CONTEXT_TOKEN);
        if(null == accessToken || accessToken == ""){
            accessToken = request.getParameter(CommonConstants.TOKEN);
        }
        if (null == accessToken) {
            setFailedRequest(R.error401(), 200);
            return null;
        }
        try {
            UserToken userToken = JwtUtils.getInfoFromToken(accessToken);
        } catch (Exception e) {
            setFailedRequest(R.error401(), 200);
            return null;
        }
        FilterContextHandler.setToken(accessToken);
        if(!havePermission(request)){
            setFailedRequest(R.error403(), 200);
            return null;
        }
        Set<String> headers = (Set<String>) ctx.get("ignoredHeaders");
        //We need our JWT tokens relayed to resource servers
        //添加自己header
//        ctx.addZuulRequestHeader(CommonConstants.CONTEXT_TOKEN, accessToken);
        //移除忽略token
        headers.remove("authorization");
        return null;
//        RequestContext ctx = RequestContext.getCurrentContext();
//        Set<String> headers = (Set<String>) ctx.get("ignoredHeaders");
//        // We need our JWT tokens relayed to resource servers
//        headers.remove("authorization");
//        return null;
    }

    private void setFailedRequest(Object body, int code) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        HttpServletResponse response = ctx.getResponse();
        PrintWriter out = null;
        try{
            out = response.getWriter();
            out.write(JSONUtils.beanToJson(body));
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
        ctx.setSendZuulResponse(false);
    }

    private boolean havePermission(HttpServletRequest request){
        String currentURL = request.getRequestURI();
        List<MenuDTO> menuDTOS = menuService.userMenus();
        for(MenuDTO menuDTO:menuDTOS){
            if(currentURL!=null&&null!=menuDTO.getUrl()&&currentURL.startsWith(menuDTO.getUrl())){
                return true;
            }
        }
        return false;
    }

    private boolean isStartWith(String requestUri) {
        boolean flag = false;
        for (String s : ignorePath.split(",")) {

            if (requestUri.startsWith(s)) {
                return true;
            }
        }
        return flag;
    }
}
