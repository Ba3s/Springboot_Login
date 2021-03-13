package com.example.demo.DBUilt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(filterName = "LoginFilter",urlPatterns = "/*")
public class LoginFilter implements Filter {

    @Autowired
    RedisTool redisTool;

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        //强制转换
        HttpServletRequest request = (HttpServletRequest) req;

        //获取资源请求路径
        String uri = request.getRequestURI();

        //判断是否包含登录的相关路径
        if (uri.contains("/LoginIndex") || uri.contains("/Login") || uri.contains("/LoginChoice") || uri.contains("/static/") || uri.contains("/templates/")){
            chain.doFilter(req,resp);
        }else {
            if (redisTool.hasKey("spring:session:sessions:"+request.getSession().getId()) && request.getSession().getAttribute("LoginStatus").equals(614207941)){
                chain.doFilter(req,resp);
            }else {
                HttpServletResponse response= (HttpServletResponse)resp;
                response.sendRedirect("templates/Index.html");
            }
        }
    }
}
