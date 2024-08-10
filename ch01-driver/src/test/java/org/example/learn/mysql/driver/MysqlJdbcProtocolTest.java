package org.example.learn.mysql.driver;

import org.example.learn.mysql.driver.config.JdbcConfig;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * mysql的通讯报文的具体数据可以看mysql-*.pcapng文件
 *
 * DriverManager#getConnection()
 *      建立tcp链接
 *      server主动发送Greeting包(告知server端的状态)
 *      client发送Login Request包(告知client端的状态,以及登录凭证)
 *      server验证通过后,回复Response-Ok包(告知connection的状态)
 *
 * Statement#executeQuery()
 *      client发送Request-Query包(sql语句)
 *      server回应Response-TABULAR包(包含column-count/field-packet/row-packet信息)
 *            和Response-Ok包(表示查询数据已经发送完)
 *
 *  Connection#close()
 *      client发送Request-Quit包(表示断开链接)
 */
public class MysqlJdbcProtocolTest {

    private JdbcConfig config;

    @Before
    public void setup() {
        config = new JdbcConfig();
        // 默认情况下,tcp通讯会ssl加密,需要主动配置不加密通讯,才能抓到明文报文
        config.setUrl("jdbc:mysql://192.168.175.129:3306/test01?useSSL=false&requireSSL=false");
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

    /**
     * 结果集非常大的情况
     */
    @Test
    public void test20() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 建立tcp链接
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        String sql = "insert into t (name) values (?)";
        // 不涉及tcp通讯,仅在jvm中创建相关的java对象
        // prepareStatement的sql因为是模板,所以在本地驱动中会将模板信息做缓存,降低不必要的开支
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        // 插入数据
        for (int i = 0; i < 100; i++) {
            preparedStatement.setString(1, "name" + i);
            int rowAffected = preparedStatement.executeUpdate();
            System.out.println("rowAffected = " + rowAffected);
        }

        // 断开tcp链接
        connection.close();
    }

    /**
     * 结果集非常大的情况
     *
     * The fetchSize parameter is a hint to the JDBC driver as to many rows to fetch in one go from the database.
     * But the driver is free to ignore this and do what it sees fit.
     *
     * fetchSize仅仅是个hint,如果server觉得结果集不大,会忽视该hint;更有甚者直接忽略该hint
     */
    @Test
    public void test21() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 建立tcp链接
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        String sql = "select * from t";
        // 不涉及tcp通讯,仅在jvm中创建相关的java对象
        // prepareStatement的sql因为是模板,所以在本地驱动中会将模板信息做缓存,降低不必要的开支
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setFetchSize(20);
        // setMaxRows等价于sql语句中的limit
//        preparedStatement.setMaxRows(3);

        // 发送jdbc报文到mysql-server,然后等待接受响应,并序列化为java对象
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println("resultSet.getFetchSize() = " + resultSet.getFetchSize());

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

    @Test
    public void test22() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 建立tcp链接
        Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        connection.setAutoCommit(false);
        String sql = "select * from t";
        // 不涉及tcp通讯,仅在jvm中创建相关的java对象
        // prepareStatement的sql因为是模板,所以在本地驱动中会将模板信息做缓存,降低不必要的开支
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setFetchSize(20);
        // setMaxRows等价于sql语句中的limit
//        preparedStatement.setMaxRows(3);

        // 发送jdbc报文到mysql-server,然后等待接受响应,并序列化为java对象
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println("resultSet.getFetchSize() = " + resultSet.getFetchSize());

        // 如果上次响应包含全部的数据,那么就不再涉及网络通讯了
        while (resultSet.next()) {
            // 根据需要获取字段值，例如：
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println(String.format("%d | %s", id, name));
        }

        connection.commit();
        // 需要恢复AutoCommit
        connection.setAutoCommit(true);

        // 断开tcp链接
        connection.close();
    }
}
