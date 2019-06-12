package com.github.pig.gateway.ex;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* **********************************************************************
 *              Created by FangWenFei on 2019/1/17.                     
 * **********************************************************************
 * **********Thunderstorm, rain and dew are all grace of heaven**********
 * **********************************************************************
 */
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws ServletException {
        Map<String, Object> map = new HashMap<String, Object>();
        Throwable cause = authException.getCause();
        if(cause instanceof InvalidTokenException) {
            map.put("code", "401");//401
            map.put("msg", "无效的token");
        }else{
            map.put("code", "401");//401
            map.put("msg", "访问此资源需要完全的身份验证");
        }
        map.put("data", authException.getMessage());
        map.put("success", false);
        map.put("path", request.getServletPath());
        map.put("timestamp", String.valueOf(new Date().getTime()));
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), map);
        } catch (Exception e) {
            throw new ServletException();
        }
}
}
