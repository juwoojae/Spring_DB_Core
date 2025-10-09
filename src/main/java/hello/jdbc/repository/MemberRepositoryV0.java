package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * PreparedStatement 는 Statement 의 자식 타입인데 ? 를 통한 파라메터 바인딩을 가능하게 해준다.
 * SQL injection 공격에 대바할수 있다
 * <p>
 * ResultSet
 * select 쿼리가 순서대로 들어간다
 * 내부의 cursot 를 이동해서 다음 데이터를 조회할수 있다.
 * rs.next() 이것을 호출하면 커서가 다음으로 이동한다. (최초의 커서는 데이터를 가지고 있지 않음 . 최초로 한번 호출해야한다)
 * rs.getString("member_id) 현재 커서가 가리키고 있는 row 의 member_id 를 반환한다.
 */
@Slf4j
public class MemberRepositoryV0 {

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
        if (rs != null) {
            try {
                rs.close(); //Exception 발생 가능
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close(); //Exception 발생 가능
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        if (con != null) {
            try {
                con.close(); //Exception 발생 가능
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return DBConnectionUtil.getConnection();
    }
}