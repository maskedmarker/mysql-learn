package org.example.learn.mysql.driver;

import org.example.learn.mysql.driver.config.JdbcConfig;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MysqlJdbcProtocolTest {

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

        // 建立tcp链接
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        String sql = "SELECT 1 from dual";
        // 不涉及tcp通讯,仅在jvm中创建相关的java对象
        Statement statement = connection.createStatement();

        // 发送jdbc报文到mysql-server,然后等待接受响应,并序列化为java对象
        ResultSet resultSet = statement.executeQuery(sql);

        // 如果上次响应包含全部的数据,那么就不再涉及网络通讯了
        while (resultSet.next()) {
            String data = resultSet.getString("1");
            System.out.println(data);
        }

        resultSet.close();
        statement.close();

        // 断开tcp链接,只有这里涉及tcp通讯,上面的close都是java逻辑
        connection.close();
    }

    @Test
    public void test1() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 建立tcp链接
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        String sql = "select * from t where id = ?";
        // 不涉及tcp通讯,仅在jvm中创建相关的java对象
        // prepareStatement的sql因为是模板,所以在本地驱动中会将模板信息做缓存,降低不必要的开支
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, 1);

        // 发送jdbc报文到mysql-server,然后等待接受响应,并序列化为java对象
        ResultSet resultSet = preparedStatement.executeQuery();
        // 如果上次响应包含全部的数据,那么就不再涉及网络通讯了
        while (resultSet.next()) {
            // 根据需要获取字段值，例如：
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println(String.format("%d | %s", id, name));
        }

        // 断开tcp链接
        connection.close();
    }
}
