//package com.learning.spring.models;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.learning.spring.dao.StudentDao;
//
//@Component
//public class ClassroomService {
//  private List<Student> students;
//  private static int idCounter = 1;
//
//  @Autowired
//  private StudentDao studentDAO;
//
//  public ClassroomService() {
//    if (students == null)
//      students = new ArrayList<>();
//  }
//
//  public void sync() {
//    students = studentDAO.readAllStudents();
//    rank();
//  }
//
//  public List<Student> getStudents() {
//    if (students.isEmpty())
//      sync();
//    return Collections.unmodifiableList(students);
//  }
//
//  private void rank() {
//    Collections.sort(students, (s1, s2) -> -Integer.compare(s1.getScore(), s2.getScore()));
//    int maxScore = students.get(0).getScore();
//    int rank = 1;
//    for (int i = 0; i < students.size(); i++) {
//      if (students.get(i).getScore() != maxScore) {
//        maxScore = students.get(i).getScore();
//        rank = i + 1;
//      }
//      students.get(i).setRank(rank);
//    }
//  }
//
//  public void add(Student student) {
//    student.setId(idCounter++);
//    students.add(student);
//    studentDAO.createStudent(student);
//    sync();
//  }
//
//  public void remove(int id) {
//    for (Student s : students) {
//      if (s.getId() == id) {
//        students.remove(s);
//        break;
//      }
//    }
//    rank();
//  }
//
//  public void replace(int id, Student current) {
//    for (int i = 0; i < students.size(); i++) {
//      if (students.get(i).getId() == id) {
//        students.get(i).setName(current.getName());
//        students.get(i).setScore(current.getScore());
//      }
//    }
//    rank();
//  }
//
//  public Optional<Student> getById(int id) {
//    for (int i = 0; i < students.size(); i++) {
//      if (students.get(i).getId() == id)
//        return Optional.of(students.get(i));
//    }
//    return Optional.empty();
//  }
//}
