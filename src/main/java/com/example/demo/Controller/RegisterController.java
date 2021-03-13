package com.example.demo.Controller;

import com.example.demo.User.UserImp.UserDaoImp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

@Controller
public class RegisterController {

    @RequestMapping("Register")
    public void CheckRegister(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String password_confirm, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        HashMap<String,Object> map = new HashMap<>();

        if (username!=null && email!=null && password!=null && password_confirm!=null){
            if (password.equals(password_confirm)){ //两次密码都一致
                if (new UserDaoImp().RegisterUser(username, password, "man", 13, "2020") == 1){
                    map.put("code","1");
                }else {
                    map.put("code","0");
                }
            }
        }else {
            map.put("code",-1);
        }
        new ObjectMapper().writeValue(response.getWriter(),map);
    }
}
