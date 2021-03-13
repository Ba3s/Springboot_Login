package com.example.demo.DBUilt;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserCheckTool {
    // \\b  表示 限定单词边界  比如  select 不通过   1select则是可以的
    static String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
            + "(\\b(select|update|union|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

    /**
     * 防御sql注入
     * @param sql
     * @return
     */
    public static boolean CheckSql(String sql){
        Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);//表示忽略大小写
        Matcher matcher = sqlPattern.matcher(sql);
            if (matcher.find()){
                return false;
            }
            return true;
    }

    /**
     * 根据时间戳生成防御csrf防御码
     * @return
     */
    public static StringBuilder CreateCsrfToken(){
        Random Random = new Random();
        String RandomStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random.setSeed(new Date().getTime());
        StringBuilder CsrfToken = new StringBuilder();
        for (int i=0;i<32;i++){
            CsrfToken.append(RandomStr.charAt(Random.nextInt(RandomStr.length())));
        }
        return CsrfToken;
    }


    /**
     * CsrfToken检测机制
     * @param CsrfToken
     * @param request
     * @return
     */
    public static boolean CheckCsrfToken(String CsrfToken, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies){
            String name = cookie.getName();
            if (name.equals("CsrfToken")){  //如果名字叫做CsrfToken就获取它的值进行比较
                if (cookie.getValue().equals(CsrfToken)){
                    return true;
                }
            }
        }
        return false;
    }
}
