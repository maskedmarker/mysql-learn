package org.example.learn.mysql.driver;

import org.example.learn.mysql.driver.config.JdbcConfig;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InsertSelectFromTest {

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
        String sql = "insert t_bak select * from t";
        Statement statement = connection.createStatement();

        // true if the first result is a ResultSet object; false if it is an update count or there are no results
        boolean execute = statement.execute(sql);
        if (!execute) {
            int updateCount = statement.getUpdateCount();
            System.out.println("updateCount = " + updateCount);
        }

        connection.close();
    }

    @Test
    public void test1() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        String sql = "insert into t_bak select * from t";
        Statement statement = connection.createStatement();

        // true if the first result is a ResultSet object; false if it is an update count or there are no results
        boolean execute = statement.execute(sql);
        if (!execute) {
            int updateCount = statement.getUpdateCount();
            System.out.println("updateCount = " + updateCount);
        }

        connection.close();
    }
}
