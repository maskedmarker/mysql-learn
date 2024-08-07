package org.example.learn.mysql.driver;

import org.example.learn.mysql.driver.config.JdbcConfig;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PreparedStatementExecuteTest {

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

        String sql = "select * from t where id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, 1);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            // 根据需要获取字段值，例如：
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println(String.format("%d | %s", id, name));
        }
    }
}
