package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    /**
     * DriverManager.getConnection() 를 사용하면 된다.
     * External library 에 있는 db Driver 해당 드라이버가 제공하는 Connection 을 반환해준다
     * 여기서는 H2 dp Driver 가 작동해서 실제 데이터베이스와 커넥션을 맺고 그 결과를 반환해준다
     * @return JDBC 표준 인터페이스가 제공하는 Connection
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class ={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

    }
}
