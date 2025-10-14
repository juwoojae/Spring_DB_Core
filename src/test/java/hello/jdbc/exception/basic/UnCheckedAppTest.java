package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

/**
 * 체크예외 -> 언체크예외(런타임 exception) 으로 전환을 해줄때에는
 * 꼭 !!  예외를 포함시켜 주어야 한다!!
 */
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
          //  e.printStackTrace();
            log.info("ex",e); //로그를 출력할때, 마지막 파라메터에 예외를 넣어주면 로그에 스택 트레이스를 출력할수 있다.
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
        } //CausedBy 가 안나온다 . 뭐때문에 발생했는지 모른다

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
