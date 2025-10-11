package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;  //JDBC 라는 특정 기술에 의존
    private final MemberRepositoryV2 memberRepositoryV2;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection con = dataSource.getConnection();
        try{
            //JDBC 라는 특정 기술에 의존
            con.setAutoCommit(false); //트랜잭션 시작(트랜잭션은 비즈니스로직이 있는 서비스 계층에서 시작하는 것이 좋다.)
            bizLogic(con, fromId, toId, money); //트랜잭션 을 관리하는 로직과 비즈니스 로직을 분리
            //JDBC 라는 특정 기술에 의존
            con.commit(); //성공시 커밋
        } catch (Exception e){
            con.rollback(); //실패시 롤백//JDBC 라는 특정 기술에 의존
            throw new IllegalStateException(e);
        }finally {
            release(con);
        }
    }

    private void release(Connection con) {
        if(con!=null){
            try{
                con.setAutoCommit(true); //자동커밋 모드로 다시 젼환한후에 릴리즈 종료
                con.close();
            } catch (Exception e) {
                log.info("error",e);
            }
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        //비즈니스 로직
        Member fromMember = memberRepositoryV2.findById(con,fromId);
        Member toMember = memberRepositoryV2.findById(con,toId);

        memberRepositoryV2.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV2.update(con, toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
/**
 * 1. 프레젠테이션 계층(Controller)
 * UI 관련 처리
 * 웹요청 응답, validation 처리
 * 주 사용 기술: 서블릿, 스프링 MVC
 * 2. 서비스 계층 (Service)
 * 비즈니스 로직을 담당
 * 주 사용 기술 : 가급적 특정 기술에 의존하지 않으며, 순수 자바코드로 작성된다
 * 3. 데이터 접근 계층
 * 실제 DB 에 접근하는 코드
 * 주 사용 기술 : JDBC, JPA, Redis,MongoDB
 * <문제점들>
 * 비즈니스 로직을 구현한 서비스 계층의 코드가 JDBC 라는 특정 기술에 의존하고 있다. JPA로 변경한다면 , 여기 서비스 코드를
 * 다갈아 엎어야함
 * 비즈니스 로직을 처리하는 코드와 try-catch 등 트랜잭션 코드를 관리하는 코드가 섞여 있다. 분리할 필요가 있어보인다
 * 예외는 데이터 접근 계층에서 처리하는것이 좋다
 */