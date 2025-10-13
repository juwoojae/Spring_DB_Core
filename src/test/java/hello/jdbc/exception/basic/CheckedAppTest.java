package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

/**
 * 체크예외 vs 언체크 예외
 * 체크 예외 보다는 언체크 예외 사용을 권장하는 이유 3가지
 * 1. 코드 노이징
 *  체크 예외를 사용하면 명시적으로 예외를 밖으로 던져 줘야하는데
 *  이렇게 되면 부수적인 코드가 많이 생긴다
 * 2. 1  의 과정에서 발생하는 의존성 누수
 * SQLDException 은 JDBC 라는 구체적인 기술에 의존한다. 이렇게
 * 예외를 던지는 코드에 명시를 해주면, 의존성 누수가 발생해서
 * 단일책임의 원칙, 의존관계 역전의 원칙 에 위배된다
 * 3. 체크 예외의 설계철학
 * 애플리케이션에서 발생하는 체크예외(언체크 예외보다 상대적으로 큰) 예외는
 * 설계철학이 빠르게 복구해서 정상적인 흐름으로 동작하게끔 하는것이지만,
 * 데이터베이스 자체에서 발생한문제, 네트워크 연결문제처럼 대부분 복구가 불가능하다.
 * 그러므로 스프링이면(디스페쳐 서블릿) 에서 서블릿 필터나, 인터셉터를 통해서 함께 묶어서 처리를 해주어야한다.
 * 주의!!
 * Exception 으로 하나로 묶어서 체크 에러로 처리하면 안될까?
 * 안됨, 처리할땐 편하지만, 예외 전파 과정에 나오는 예외는 Exception 에 종속되므로 무시될수 있다.
 * 안티패턴이므로 사용하지 말자!!
 */
public class CheckedAppTest {

    @Test
    void checked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }

    static class Controller {
        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        //서비스에서 처리못하므로 밖으로 던지기
        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }
}
