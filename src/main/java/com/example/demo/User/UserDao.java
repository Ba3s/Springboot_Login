package com.example.demo.User;

import com.example.demo.User.UserImp.User;

import java.sql.SQLException;

public interface UserDao {
    /**
     * 获取用户信息
     * @param uname
     * @param password
     * @return 返回一个查询出来的用户对象
     */
    User GetUserInfo(String uname, String password);

    /**
     * 用户注册接口
     * @param uname
     * @param password
     * @param sex
     * @param age
     * @param brithday
     * @return  若注册成功返回整数
     */
    int RegisterUser(String uname,String password,String sex,int age,String brithday);
}
