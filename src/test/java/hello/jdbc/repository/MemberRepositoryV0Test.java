package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException{
        //save
        Member member = new Member("memberV4", 10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        log.info("member == findMember {}", member == findMember); //참조를 같은것을해야 true 를 반환한다.
        log.info("member equals findMember {}", member.equals(findMember)); //equals() 를 @Data lombok 에서 오버라이딩해서 선언해둠. 속성 끼리 비교해서, 모두 참이라면 true 를 반환한다
        Assertions.assertThat(findMember).isEqualTo(member);
    }

}