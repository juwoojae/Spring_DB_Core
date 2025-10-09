package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.UserCredentialsDataSourceAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException{
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD); //새로운 Connection 생성
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }

    /**
     * 설정과 사용의 분리
     * 설정과 관계 없이 DataSource 의 getConnection() 만 호출해서 사용하면 된다
     */
    @Test
    void dataSourceDriverMannager() throws SQLException {
        //DriverMannagerDataSource - 항상 새로운 Connection 을 얻는다
        DataSource dataSource =new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    /**
     * HikariDataSource 는 DataSource 인터페이스의 구현체이다
     * 커넥션풀에서 커넥션을 생성하는 작업은 애플리케이션 실행속도와 독립적으로 별개의 스레드에서 실행된다.
     * 그래서 테스트가 끝나도 계속 로그가 생김을 확인할수 있다
     */
    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException{
        //커넥션 풀링 : HikariProxyConnection(Proxy) -> JdbcConnection(Target)
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10); //커넥션 풀 사이즈를 10으로 지정
        dataSource.setPoolName("MyPool"); //풀의 이름을 MyPool 이라고 지정

        useDataSource(dataSource);
        Thread.sleep(1000);
    }
    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }
}

