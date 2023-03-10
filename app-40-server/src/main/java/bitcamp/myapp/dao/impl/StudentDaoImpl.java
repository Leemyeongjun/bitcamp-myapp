package bitcamp.myapp.dao.impl;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import bitcamp.myapp.dao.DaoException;
import bitcamp.myapp.dao.StudentDao;
import bitcamp.myapp.vo.Student;
import bitcamp.util.ConnectionFactory;

public class StudentDaoImpl implements StudentDao {

  ConnectionFactory conFactory;

  public StudentDaoImpl(ConnectionFactory conFactory) {
    this.conFactory = conFactory;
  }

  @Override
  public void insert(Student s) {

    try (Statement stmt = conFactory.getConnection().createStatement()) {

      String sql = String.format("insert into app_student("
          + "  member_id,"
          + "  pst_no,"
          + "  bas_addr,"
          + "  det_addr,"
          + "  work,"
          + "  gender,"
          + "  level)"
          + " values('%s','%s','%s','%s',%b,'%s',%d)",
          s.getNo(), // app_member 테이블에 입력한 후 자동 생성된 PK 값
          s.getPostNo(),
          s.getBasicAddress(),
          s.getDetailAddress(),
          s.isWorking(),
          s.getGender(),
          s.getLevel());

      stmt.executeUpdate(sql);

    } catch (Exception e) {
      throw new DaoException(e);
    }
  }

  @Override
  public List<Student> findAll() {
    try (Statement stmt = conFactory.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select"
            + "  m.member_id,"
            + "  m.name,"
            + "  m.email,"
            + "  m.tel,"
            + "  s.work,"
            + "  s.level"
            + " from app_student s"
            + "   inner join app_member m on s.member_id = m.member_id"
            + " order by"
            + "   m.name asc")) {

      ArrayList<Student> list = new ArrayList<>();
      while (rs.next()) {
        Student s = new Student();
        s.setNo(rs.getInt("member_id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setTel(rs.getString("tel"));
        s.setWorking(rs.getBoolean("work"));
        s.setLevel(rs.getByte("level"));

        list.add(s);
      }
      return list;

    } catch (Exception e) {
      throw new DaoException(e);
    }
  }

  @Override
  public Student findByNo(int no) {
    try (Statement stmt = conFactory.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select"
            + "  m.member_id,"
            + "  m.name,"
            + "  m.email,"
            + "  m.tel,"
            + "  m.created_date,"
            + "  s.pst_no,"
            + "  s.bas_addr,"
            + "  s.det_addr,"
            + "  s.work,"
            + "  s.gender,"
            + "  s.level"
            + " from app_student s"
            + "   inner join app_member m on s.member_id = m.member_id"
            + " where s.member_id=" + no)) {

      if (rs.next()) {
        Student s = new Student();
        s.setNo(rs.getInt("member_id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setTel(rs.getString("tel"));
        s.setCreatedDate(rs.getDate("created_date"));
        s.setPostNo(rs.getString("pst_no"));
        s.setBasicAddress(rs.getString("bas_addr"));
        s.setDetailAddress(rs.getString("det_addr"));
        s.setWorking(rs.getBoolean("work"));
        s.setGender(rs.getString("gender").charAt(0));
        s.setLevel(rs.getByte("level"));
        return s;
      }

      return null;

    } catch (Exception e) {
      throw new DaoException(e);
    }
  }

  @Override
  public List<Student> findByKeyword(String keyword) {
    try (Statement stmt = conFactory.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select"
            + "  m.member_id,"
            + "  m.name,"
            + "  m.email,"
            + "  m.tel,"
            + "  s.work,"
            + "  s.level"
            + " from app_student s"
            + "   inner join app_member m on s.member_id = m.member_id"
            + " where"
            + "   m.name like('%" + keyword + "%')"
            + "   or m.email like('%" + keyword + "%')"
            + "   or m.tel like('%" + keyword + "%')"
            + "   or s.bas_addr like('%" + keyword + "%')"
            + "   or s.det_addr like('%" + keyword + "%')"
            + " order by"
            + "   m.member_id desc")) {

      ArrayList<Student> list = new ArrayList<>();
      while (rs.next()) {
        Student s = new Student();
        s.setNo(rs.getInt("member_id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setTel(rs.getString("tel"));
        s.setWorking(rs.getBoolean("work"));
        s.setLevel(rs.getByte("level"));

        list.add(s);
      }
      return list;

    } catch (Exception e) {
      throw new DaoException(e);
    }
  }

  @Override
  public int update(Student s) {
    try (Statement stmt = conFactory.getConnection().createStatement()) {

      String sql = String.format("update app_student set "
          + "  pst_no='%s',"
          + "  bas_addr='%s',"
          + "  det_addr='%s',"
          + "  work=%b,"
          + "  gender='%s',"
          + "  level=%d "
          + " where member_id=%d",
          s.getPostNo(),
          s.getBasicAddress(),
          s.getDetailAddress(),
          s.isWorking(),
          s.getGender(),
          s.getLevel(),
          s.getNo());

      return stmt.executeUpdate(sql);

    } catch (Exception e) {
      throw new DaoException(e);
    }
  }

  @Override
  public int delete(int no) {
    try (Statement stmt = conFactory.getConnection().createStatement()) {

      String sql = String.format("delete from app_student"
          + " where member_id=%d", no);
      return stmt.executeUpdate(sql);

    } catch (Exception e) {
      throw new DaoException(e);
    }
  }
}























