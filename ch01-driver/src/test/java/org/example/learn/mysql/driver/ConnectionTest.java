package org.example.learn.mysql.driver;

import org.example.learn.mysql.driver.config.JdbcConfig;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConnectionTest {

    private JdbcConfig config;

    @Before
    public void setup() {
        config = new JdbcConfig();
        config.setUrl("jdbc:mysql://192.168.175.129:3306/test01");
        config.setUsername("root");
        config.setPassword("123456");
    }

    @Test
    public void test0() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());

        String sql = "SELECT 1 from dual";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            // 根据需要获取字段值，例如：
            String data = resultSet.getString("1");
            System.out.println(data);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
