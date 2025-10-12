package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {
    //private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;
    //관례적으로 PlatformTransactionManager 로 생성자 주입받는다
    //PlatformTransactionManager 를 SpringBean 으로 등록해서 DI를 해줄수도 있지만, 관념적으로 이렇게 쓴다
    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    /**
     * 트랜잭션 템플릿을 사용해서 트랜잭션을 시작하고, 커밋,롤백코드를 자동으로 처리해준다
     * 비즈니스로직이 정상적으로 수행되면 커밋한다
     * 언체크 예외가 발생하면 롤백한다(체크 예외는 커밋으로 처리한다, 그러므로 따로 catch 해서 런타임에러를 던저줘야한다)
     */
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        txTemplate.executeWithoutResult((status)->{
            try{
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
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