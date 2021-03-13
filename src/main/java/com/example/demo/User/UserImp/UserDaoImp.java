package com.example.demo.User.UserImp;

import com.example.demo.DBUilt.DBTool;
import com.example.demo.User.UserDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImp implements UserDao {

    /**
     * 实现查询用户信息并返回
     * @param uname
     * @param password
     * @return
     */
    @Override
    public User GetUserInfo(String uname, String password) {
        //声明数据库参数变量
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;

        //声明数据变量
        User user=null;

        try {
            //获取连接
            connection = DBTool.GetConnection();
            //创建sql命令
            String sql = "select * from t_user where uname=? and password=?";
            //创建sql命令对象
            preparedStatement = connection.prepareStatement(sql);
            //给占位符赋值
            preparedStatement.setString(1,uname);
            preparedStatement.setString(2,password);
            //执行sql语句,获取到查询对象
            resultSet = preparedStatement.executeQuery();
            if (resultSet != null){
                while (resultSet.next()){
                    //给变量赋值
                    user = new User();
                    user.setUname(resultSet.getString("uname"));
                    user.setPwd(resultSet.getString("password"));
                    user.setSex(resultSet.getString("sex"));
                    user.setAge(resultSet.getInt("age"));
                    user.setBirthday(resultSet.getString("brithday"));
                }
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }finally {
            //无论如何也要释放资源
            DBTool.ConnectionClose(resultSet,preparedStatement,connection);
        }

        return user;
    }

    /**
     * 用户注册功能
     * @param uname
     * @param password
     * @param sex
     * @param age
     * @param brithday
     * @return 返回int,若返回值为-1,则注册失败.若返回1则注册成功
     */
    @Override
    public int RegisterUser(String uname, String password, String sex, int age, String brithday) {
        String sql = "insert into t_user values(?,?,?,?,?)";
        return DBTool.executeDML(sql, uname, password, sex, age, brithday);
    }
}
