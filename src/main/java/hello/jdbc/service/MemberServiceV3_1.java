package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {
    //트랜잭션 매니저를 주입 받는다. 지금은 JDBC 기술을 사용하므로 DataSourceTransactionManager 를 주입 받아야 한다.
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        //트랜잭션 시작 현재 트랜잭션 상태 정보가 포함되어 있다. 이후 트랜잭션을 커밋, 롤백할 때 필요하다.
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            bizLogic(fromId, toId, money); //트랜잭션 을 관리하는 로직과 비즈니스 로직을 분리
            transactionManager.commit(status);

        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
    }



    private void bizLogic( String fromId, String toId, int money) throws SQLException {
        //비즈니스 로직
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
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
 * 1. 비즈니스 로직을 구현한 서비스 계층의 코드가 JDBC 라는 특정 기술에 의존하고 있다. JPA로 변경한다면 , 여기 서비스 코드를
 * 2. 다갈아 엎어야함
 * 3. 트랜잭션 처리코드는 있어야하지만 데이터 접근계층에서 처리해야할 커넥션에대한 처리부분이 들어간다
 * 예외는 데이터 접근 계층에서 처리하는것이 좋다
 * <해결 방법>
 * 1. 트랜잭션 추상화
 * 2. 트랜잭션 동기화
 */