//package com.learning.spring.dao;
//
//import java.util.List;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import com.learning.spring.models.Student;
//
//@Repository
//public class StudentDao {
//    private static final String STUDENT_LIST = "select * from Students";
//    private static final String READ_STUDENT = "select * from Students where id=?";
//    private static final String CREATE_STUDENT = "insert into Students(name, score) values(?, ?)";
//    private static final String UPDATE_STUDENT = "update Students set name=?, score=? where id=?";
//    private static final String DELETE_STUDENT = "delete from Students where id=?";
//    private final JdbcTemplate jdbcTemplate;
//
//    public StudentDao(@Qualifier("mydbJdbcTemplate") JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    public int createTable() {
//        String query = "CREATE TABLE IF NOT EXISTS Students(id int primary key auto_increment, name varchar(255), score int)";
//        System.out.println("table created....");
//        return jdbcTemplate.update(query);
//    }
//
//    public Student findById(int id) {
//        return jdbcTemplate.query(READ_STUDENT, rs -> {
//            rs.next();
//            int readId = rs.getInt("id");
//            int score = rs.getInt("score");
//            String name = rs.getString("name");
//            Student student = new Student();
//            student.setId(readId);
//            student.setName(name);
//            student.setScore(score);
//            return student;
//        }, id);
//    }
//
//    public List<Student> readAllStudents() {
//        return jdbcTemplate.query(STUDENT_LIST, (rs, rowNum) -> {
//            int readId = rs.getInt("id");
//            int score = rs.getInt("score");
//            String name = rs.getString("name");
//            Student student = new Student();
//            student.setId(readId);
//            student.setName(name);
//            student.setScore(score);
//            return student;
//        });
//    }
//
//    public int createStudent(Student student) {
//        return jdbcTemplate.update(CREATE_STUDENT, student.getName(), student.getScore());
//    }
//
//    public int deleteStudent(Student student) {
//        return jdbcTemplate.update(DELETE_STUDENT, student.getId());
//    }
//
//    public int editStudent(Student student) {
//        return jdbcTemplate.update(UPDATE_STUDENT, student.getName(), student.getScore(), student.getId());
//    }
//
//}
