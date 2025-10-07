package hello.jdbc.connection;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;
public class DBConnectionUtilTest {

    /**
     * org.h2.jdbc.JdbcConnection 라는 구현체가 표준 Jdbc 인터페이스의 Connection 을 구현한다
     * JDBC 가 제공하는 DriverMannager 는 라이브러리에 등록된 DB 드라이버들을 관리하고, 커넥션을 흭득한다
     * 관리하고있는 드라이버들에게 순차적으로 URL,User,Password 등 접속에 필요한 정보들을 넘긴다
     * 이렇게 드라이버를 찾은후 , 클라이언트에게 Connection 객체로 반환한다
     * @throws SQLException
     */
    @Test
    void connection() throws SQLException {
        Connection connection = DBConnectionUtil.getConnection();
        assertThat(connection).isNotNull();

    }
}
