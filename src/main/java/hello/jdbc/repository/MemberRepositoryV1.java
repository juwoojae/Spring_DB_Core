package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;
/**
 * JDBC - DataSource 사용, JdbcUtils 사용
 * DataSource 의존관계 주입(생성자 주입)
 * DataSource 는 표준 인터페이스이다
 * 커넥션을 얻는 법은 여러가지다,DriverManager 를 사용해서 항상 새로운 커넥션을 만들어서 얻는방법, 커넥션 풀을 사용하는 방법(히카리cp)
 * 이것을 얻는 getConnection 메서드를 가지는 표준 인터페이스를 Datasource 라고 한다 (커넥션을 획득하는 방법을 추상화)
 * JdbcUtils 편의 메서드를
 * 제공해서 커넥션을 편리하게 Close() 리소스 반환을 할수 있다.
 */
@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection(); //해당 h2 드라이버의 커넥션 객체를 생성
            pstmt = con.prepareStatement(sql); //데이터 베이스에 전달할 sql 문 을 준비하는 과정
            pstmt.setString(1, member.getMemberId()); //sql 문 의 ? 값을 지정한다
            pstmt.setInt(2, member.getMoney());  //sql 문 의 두번째 ? 값을 지정한다
            pstmt.executeUpdate(); //Statement 를 통해 준비된 SQL 을 커넥션을 통해 실제 데이터 베이스에 전달한다
            return member;
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId); //해당 데이터베이스의 쿼리문의 ? 에 바인딩하기

            rs = pstmt.executeQuery(); //데이터를 변경할때는 executeUpdate() 를 사용하지만, 조회할때는 executeQurey() 를 사용한다
            if (rs.next()) { //resultSet 에서 찾은 Member 를 가지고 오는 과정 (커서가 존재해서 , 하나 다음칸으로 진행해야한다)
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        }  finally {
        close(con, pstmt, null);
    }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";  //개별행을 where 로 찾은 후 set 으로 갱신하기

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate(); //쿼리를 실행하고 영향받은 row 수를 반환한다
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            pstmt.executeUpdate();
    }catch (SQLException e){
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    /**
     * 리소스 정리 메서드
     * 리소스를 정리할때에는 항상 역순으로 적용해주어야 한다
     */
    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);

    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}