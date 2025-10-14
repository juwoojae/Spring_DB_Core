package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JdbcTemplate 사용
 * 커넥션 닫기, 커넥션 동기화 datasourse 다필요 없음
 * 예외 발생 시에 스프링 예외 변환기 도 자동으로 생성해준다
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository {

    private final JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values (?,?)";
        template.update(sql, member.getMemberId(), member.getMoney()); //sql 문장 + 파라메터 1, 파라메터 2
        return member;
    }

    //메서드 오버로딩
    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";
        return  template.queryForObject(sql, memberRowMapper(), memberId);//한건 조회하는것.
    }

    private RowMapper<Member> memberRowMapper() {
        return(rs, rowNum)->{
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";  //개별행을 where 로 찾은 후 set 으로 갱신하기
        template.update(sql, money, memberId);
    }

    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";
        template.update(sql, memberId);
    }

}