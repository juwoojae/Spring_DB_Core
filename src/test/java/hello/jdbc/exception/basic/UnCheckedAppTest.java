package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Slf4j
public class UnCheckedAppTest {

    @Test
    void unchecked(){
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }
    @Test
    void printEx(){
        Controller controller = new Controller();
        try{
            controller.request();
        } catch (Exception e) {
            log.info("ex",e);
        }
    }

    static class Controller{
        Service service = new Service();

        public void request(){
            service.logic();
        }
    }

    static class Service{
        Repository repository = new Repository();
        NetWorkClient netWorkClient = new NetWorkClient();

        public void logic(){
            repository.call();
            netWorkClient.call();
        }
    }

    static class NetWorkClient{
        public void call(){
            throw new RuntimeConnectionException("연결 실패");
        }
    }

    static class Repository{
        public void call(){
            try{
                runSQL();
            }catch (SQLException e){
                throw new RuntimeSQLException(e);//체크 예외를 생성자를 넣어 줌으로서, 로그에 체크예외, 변경한 언체크 예외까지 같이 뜬다.
            }
        }

        private void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectionException extends RuntimeException{
        public RuntimeConnectionException(String message){
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException{
        public RuntimeSQLException() {
        }

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
