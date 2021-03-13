package com.example.demo.LoginServer;

import com.example.demo.DBUilt.UserCheckTool;

public class LoginMessage {
    private String uname;
    private String password;
    private String csrfToken;

    public String getPassword() {
        return password;
    }

    public String getUname() {
        return uname;
    }

    public String getCsrfToken(){
        return csrfToken;
    }

    public void setCsrfToken(){
        StringBuilder stringBuilder = UserCheckTool.CreateCsrfToken();
        this.csrfToken = String.valueOf(stringBuilder);   //转换为字符串
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void toString(String uname,String password){
        System.out.println("[用户名:"+uname+" .密码:"+password+"]");
    }
}
