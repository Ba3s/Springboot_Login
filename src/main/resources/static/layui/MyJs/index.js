/**
 * 表单传输
 */
function doPost() {
    //获取上面的输入
    let uname = document.getElementById("userName").value;
    let password= document.getElementById("password").value;
    let csrfToken = document.getElementById("csrfToken").value;

    //使用jQuery的ajax技术
    $.post("/Login",{"uname":uname,"password":password,"csrfToken":csrfToken},function (res){
        if (res.code>0){
            layer.confirm(res.msg,{btn:["将其下线","允许多用户登录"],  //https://www.layui.com/doc/modules/layer.html
                btn1:function (){
                    $.post("/LoginChoice",{"choice_id":1,"uname":uname,"password":password},function (data) {   //存在危险,因为用户的姓名是用户名来命名的,别人也知道。那么如果只用提交uname,就可以直接访问UserChoice写入session,所以要加上password
                        if (data.code==1){
                            layer.msg(data.msg);
                            setTimeout(function () {    //3秒后执行
                                $.post("/User",{"uname":uname,"password":password});
                                location.href="http://localhost:8080/User"; //定向到主页
                            },3000);
                        }else if (data.code==-1){
                            layer.msg(data.msg,{icon:5});
                        }else {
                            layer.msg("登录时出现不可预估错误",{icon:7});
                        }
                    },"json")
                },
                btn2:function (){
                    $.post("/LoginChoice",{"choice_id":2,"uname":uname,"password":password},function (data)  {
                        if (data.code==2){
                            layer.msg(data.msg);
                            setTimeout(function () {    //3秒后执行
                                location.href="http://localhost:8080/User"; //定向到主页
                            },3000);
                        }else if (data.code==-1){
                            layer.msg(data.msg,{icon:5});
                        }else {
                            layer.msg("登录时出现不可预估错误",{icon:7});
                        }
                    },"json")
                }
            });
        }else if(res.code==0) {
            layer.msg("未查询到输入用户的信息,请核实!",{icon:2});
        }else {
            layer.msg("参数请求错误!",{icon:2});
        }
    },"json");
}

function doRegisterPost() {
    //获取用户登录的数据
    let usernamesignup = document.getElementById("usernamesignup");
    let emailsignup = document.getElementById("emailsignup");
    let passwordsignup = document.getElementById("passwordsignup");
    let passwordsignup_confirm = document.getElementById("passwordsignup_confirm");

    if (passwordsignup == passwordsignup_confirm){  //两次输入的密码一致
        $.post("/Register",{"username":usernamesignup,"email":emailsignup,"password":passwordsignup,"password_confirm":passwordsignup_confirm},function (data) {
            if (data.code==1){
               layer.msg("注册成功!");
            }else if (data.code==-1){
                layer.msg("两次密码输入不一致!");
            }else if (data.code==0){
                layer.msg("注册失败!");
            }else {
                layer.msg("操作错误!")
            }
        },"json");
    }else {
        layer.msg("两次密码输入不一致!")
    }

}

