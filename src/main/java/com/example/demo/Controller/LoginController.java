package com.example.demo.Controller;

import com.example.demo.DBUilt.RedisTool;
import com.example.demo.LoginServer.LoginMessage;
import com.example.demo.User.UserImp.User;
import com.example.demo.User.UserImp.UserDaoImp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.DBUilt.UserCheckTool;

import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


@Controller
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class LoginController extends HttpServlet {


    @Autowired
    RedisTool redisTool;


    @ModelAttribute("loginMessage")
    public LoginMessage loginMessage(){
        return new LoginMessage();
    }


    /**
     * 检测登录的用户数量
     * @param uname
     * @return 返回登录该账户的人数,若没有则返回0
     */
    private long CheckUserMember(String uname){
        if (redisTool.hasKey(uname)){
            return redisTool.lGetListSize(uname);   //返回用户数量
        }
        return 0;
    }

    /**
     * 从redis中取出列表并查找是否存在SpringSession值
     * @param list
     * @param SpringSessionId
     * @return  存在返回true,不存在返回false
     */
    public boolean CheckListMember(List list,String SpringSessionId){
        for (int i=0;i<list.size();i++){
            if (list.get(i).equals(SpringSessionId)){
                return true;
            }
        }
        return false;
    }

    /**
     * 设置他们的过期时间,因为无法直接删除session
     * @param sets
     */
    private void DeleteSession(Set<String> sets){
        for (String set : sets){
            redisTool.expire(set,1);
        }

    }

    /**
     * 给界面cookie绑定session
     * @param loginMessage
     * @param response
     */
    public void setCookie(LoginMessage loginMessage, HttpServletResponse response){
        Cookie csrfToken = new Cookie("CsrfToken", loginMessage.getCsrfToken());
        response.addCookie(csrfToken);
    }



    @RequestMapping(value = "/LoginIndex",method = RequestMethod.GET)
    public String Login(@ModelAttribute LoginMessage loginMessage, Model model, HttpServletResponse response,HttpSession session){
        if (session.getAttribute("LoginStatus") != null){
            System.out.println(session.getAttribute("LoginStatus"));
        }
        loginMessage.setCsrfToken();    //绑定对象 loginMessage.CsrfToken属性
        setCookie(loginMessage,response);   //网页添加 cookie
        model.addAttribute("LoginMessage", loginMessage);
        return "LoginSuccess";
    }

    /** @ModelAttribute LoginMessage loginMessage
     * @RequestParam：获取请求的参数：若不存在，将抛出异常
     * @RequestHeader：获取请求头信息：
     * @CookieValue：获取cookie（饼干）中的值：
     * @param csrfToken 用户输入
     * @param CsrfToken 表单自带
     */
    @RequestMapping(value = "/Login",method = RequestMethod.POST)
    @ResponseBody
    public void Login (@ModelAttribute LoginMessage loginMessage,@CookieValue("CsrfToken") String CsrfToken, @RequestParam("csrfToken") String csrfToken ,HttpServletRequest request,HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");

        //获取用户输入
        String uname = request.getParameter("uname");
        String password = request.getParameter("password");

        System.out.println(uname);
        System.out.println(password);

        if (UserCheckTool.CheckSql(uname) && UserCheckTool.CheckSql(password) && CsrfToken.equals(csrfToken)){
            //创建输出到view界面的json对象
            HashMap<String, Object> map = new HashMap<>();

            //查找用户信息,是否存在用户输入的用户信息,若存在才能进入登录程序
            User user = new UserDaoImp().GetUserInfo(uname, password);
            if (user != null){  //不为空,即存在,则进入登录程序
                System.out.println(user);
                HttpSession session = request.getSession(); //给予登录的session作为标识符

                map.put("code", 1);
                map.put("msg", "该账户目前使用人数："+CheckUserMember(uname));
                //传输到客户端
                new ObjectMapper().writeValue(response.getWriter(),map);
            }else {
                map.put("code", 0);
                new ObjectMapper().writeValue(response.getWriter(),map);
            }
        }else {
            System.out.println("疑似黑客攻击!");
        }
        System.out.println(csrfToken+" "+CsrfToken);
    }

    /**
     * 用于用户选择是否多人登录还是个人登录情况
     * @param choice_id
     * @param uname
     * @param response
     * @param request
     * @throws IOException
     */
    @RequestMapping(value = "/LoginChoice",method = RequestMethod.POST)
    public void LoginChoice(@RequestParam String choice_id,@RequestParam String uname, @RequestParam String password, HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setCharacterEncoding("UTF-8");
        HashMap<String, Object> map = new HashMap<>();

        if (choice_id.equals("1") && uname!=null && password!=null && new UserDaoImp().GetUserInfo(uname,password)!=null){    //如果传递状态码为1,并获取到了登录人信息,将其他人退出
            Set<String> keys = redisTool.Keys("*"); //获取所有的键值
            DeleteSession(keys);
            redisTool.del(uname);   //退出其他人的session
            //如果redis里不存在该用户的session,则写入
            if (!CheckListMember(redisTool.lGet(uname,0,redisTool.lGetListSize(uname)),"spring:session:sessions:" + request.getSession().getId()+" :"+request.getHeader("User-Agent"))){
                request.getSession().setAttribute("LoginStatus",614207941); //登录设置,为了过滤器好识别
                redisTool.lSet(uname,"spring:session:sessions:"+request.getSession().getId()+" :"+request.getHeader("User-Agent"),1800);   //放入列表,好识别有多少人正在登录
                System.out.println(request.getSession().getId() + request.getRemoteAddr());
                map.put("code",1);
                map.put("msg","其他人已被迫登出!");
            }else if (CheckListMember(redisTool.lGet(uname,0,redisTool.lGetListSize(uname)),"spring:session:sessions:" + request.getSession().getId()+" :"+request.getHeader("User-Agent")) && request.getSession().getAttribute("LoginStatus").equals(614207941)){  //说明已经登录过了
                //已经登录过了的
                map.put("code",1);
                map.put("msg","其他人已被迫登出!");
            }
        }else if (choice_id.equals("2") && uname!=null && password!=null && new UserDaoImp().GetUserInfo(uname,password)!=null){  //如果传递状态码为2,并获取到了登录人信息,允许多人登录
            //如果redis里不存在该用户的session,则写入
            if (!CheckListMember(redisTool.lGet(uname,0,redisTool.lGetListSize(uname)),"spring:session:sessions:" + request.getSession().getId()+" :"+request.getHeader("User-Agent"))){
                request.getSession().setAttribute("LoginStatus",614207941); //写入session
                redisTool.lSet(uname,"spring:session:sessions:"+request.getSession().getId()+" :"+request.getHeader("User-Agent"),36000);
                System.out.println(request.getSession().getId() + request.getRemoteAddr());

                map.put("code",2);
                map.put("msg","正在登录,请稍后...");
            }else if (CheckListMember(redisTool.lGet(uname,0,redisTool.lGetListSize(uname)),"spring:session:sessions:" + request.getSession().getId()+" :"+request.getHeader("User-Agent")) && request.getSession().getAttribute("LoginStatus").equals(614207941)){
                map.put("code",2);
                map.put("msg","正在登录,请稍后...");
            }
        }else {
            map.put("code",-1);
            map.put("msg","操作错误!");
        }
        new ObjectMapper().writeValue(response.getWriter(),map);
    }

    @RequestMapping(value = "/User")
    public String User(){
        return "LoginSuccess";
    }

}

