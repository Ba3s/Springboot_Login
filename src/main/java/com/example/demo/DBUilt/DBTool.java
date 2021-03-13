package com.example.demo.DBUilt;

import ch.qos.logback.core.db.dialect.DBUtil;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBTool {
    private static String url;
    private static String username;
    private static String password;
    private static String driver;

    static {
        Properties properties = new Properties();
        try {
            //导入配置文件
            properties.load(DBTool.class.getClassLoader().getResourceAsStream("application.properties"));

            //获取配置文件信息
            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            driver = properties.getProperty("driver");

            //导入驱动
            Class.forName(driver);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回连接
     * @return
     * @throws SQLException
     */
    public static Connection GetConnection() throws SQLException, ClassNotFoundException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 释放资源
     * @param resultSet
     * @param preparedStatement
     * @param connection
     */
    public static void ConnectionClose(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection){
        if (resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (preparedStatement != null){
            try {
                preparedStatement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (connection != null){
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * 封装增加删除修改的通用工具方法,不需要再次获取连接
     * @param sql
     * @param objs
     * @return
     */
    public static int executeDML(String sql,Object...objs){
        // 声明jdbc变量
        Connection conn = null;
        PreparedStatement ps = null;
        int i = -1;
        try {
            // 获取连接对象
            conn = DBTool.GetConnection();
            // 开启事务管理
            conn.setAutoCommit(false);
            // 创建SQL命令对象
            ps = conn.prepareStatement(sql);
            // 给占位符赋值
            if(objs!=null){
                for(int j=0;j<objs.length;j++){
                    ps.setObject(j+1,objs[j]);
                }
            }
            // 执行SQL
            i = ps.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DBTool.ConnectionClose(null, ps, conn);
        }
        return i;
    }

}
