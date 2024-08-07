package org.example.learn.mysql.driver;

import org.example.learn.mysql.driver.config.JdbcConfig;
import org.junit.Before;
import org.junit.Test;

import javax.swing.plaf.SliderUI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class StatementExecuteTest {

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
        Statement statement = connection.createStatement();

        // 查询类的sql,调用executeQuery方法;update类的sql调用
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            // 根据需要获取字段值，例如：
            String data = resultSet.getString("1");
            System.out.println(data);
        }

        resultSet.close();
        statement.close();
        connection.close();
    }

    @Test
    public void test1() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());

        String sql = "update t set name = 'aa' where id = 1";
        Statement statement = connection.createStatement();

        // 查询类的sql,调用executeQuery方法;update类的sql调用
        int rowCount = statement.executeUpdate(sql);
        System.out.println("rowCount = " + rowCount);

        connection.close();
    }

    @Test
    public void test2() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());

        String sql = "insert t_bak select * from t";
        Statement statement = connection.createStatement();

        // 对于动态sql,因为不知道sql的类型是select/update,可以调用模糊的方法execute
        // true if the first result is a ResultSet object; false if it is an update count or there are no results
        boolean execute = statement.execute(sql);
        if (!execute) {
            int updateCount = statement.getUpdateCount();
            System.out.println("updateCount = " + updateCount);
        }

        connection.close();
    }
}
