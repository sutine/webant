package org.webant.extension.test.derby;

import java.sql.*;
import java.util.Properties;

public class DerbyTestEmbedded {
    private final static String DB_URL = "jdbc:derby:data/derby/data;create=true";
//    private final static String DERBY_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    private final static String DERBY_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    public static void main(String[] args) {
        Connection conn = null;
        try {
            Class.forName(DERBY_DRIVER);
            Properties properties = new Properties();
            // properties.put("create", "false"); // 新建数据库
            // properties.put("user", "APP");     // 用户名
            // properties.put("password", "APP"); // 密码

            // properties.put("retreiveMessagesFromServerOnGetMessage", "true");
            conn = DriverManager.getConnection(DB_URL, properties);

            Statement stat = conn.createStatement();
            ResultSet result = stat.executeQuery("SELECT * FROM ijtest");

            while (result.next()) {
                System.out.println("序号 : " + result.getInt(1));
            }
            result.close();
            stat.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 内嵌模式数据库操作用完之后需要关闭数据库,这里没有执行数据库名称则全部关闭.
            try {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (SQLException e) {
                e.getMessage();
            }
        }
    }}
