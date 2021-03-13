package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SayHelloController {


    /**
     * 针对/sayhello的GET请求，我们返回提交表单的页面，即sayHello.html
     * @param model
     * @return
     */
    @RequestMapping(value = "/sayhello", method = RequestMethod.GET)
    public String sayhello(Model model){
        //告诉页面绑定一个空的 HelloMessage 对象,这样 sayHello.html 页面初始时就会显示一个空白的表单。
        model.addAttribute("helloMessage",new HelloMessage());
        return "sayhello";
    }

    /**
     * 针对/sayhello的POST请求，我们进行表单的处理，然后将打招呼的信息渲染到message.html页面返回。
     * @param helloMessage
     * @param model
     * @return
     */
    @RequestMapping(value = "/sayhello", method = RequestMethod.POST)
    public String sayHello(@ModelAttribute HelloMessage helloMessage,Model model){
        model.addAttribute("helloMessage",helloMessage);
        return "message";
    }
}
