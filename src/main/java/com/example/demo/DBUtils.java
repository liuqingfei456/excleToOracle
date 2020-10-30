package com.example.demo;

import java.sql.*;

public class DBUtils {
    private static DBUtils db;// 保证整个系统中只存在DB的一个对象db

    static {// 当使用该类时首先执行static中的语句
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private DBUtils() {
    }// 单例模式不允许其他类创建对象

    public static Connection getConn() {
        Connection conn = null;
        String url = "jdbc:oracle:thin:@47.93.218.60:1521:orcl";
        // 数据库连接，oracle代表链接的是oracle数据库；thin:@10.218.49.217代表的是数据库所在的IP地址；
        // 1521代表链接数据库的端口号；KTSC代表的是数据库名称
        String UserName = "his_cqwy";// 数据库用户登陆名
        String Password = "test_2019";// 密码
        try {
            conn = DriverManager.getConnection(url, UserName, Password);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConn(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static PreparedStatement getPstmt(Connection conn, String sql) {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pstmt;
    }

    public static void closePstmt(PreparedStatement pstmt) {
        try {
            if (pstmt != null) {
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
