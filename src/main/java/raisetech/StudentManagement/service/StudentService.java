package raisetech.StudentManagement.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.repositry.StudentRepository;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> searchStudentList() {
    /* 検索処理
    repository.search();*/
    // 絞り込みをして、年齢が30代の人のみ抽出する。抽出したリストをControllerに返す。
    return repository.search().stream()
        .filter(student -> student.getAge() >= 30 && student.getAge() < 40)
        .collect(Collectors.toList());
  }

  public List<StudentsCourses> searchStudentsCoursesList() {
    // 絞り込み検索で、「Java」コース情報のみを抽出する。抽出したリストをControllerに返す。
    return repository.searchStudentsCourses().stream()
        .filter(studentsCourses -> "Javaコース".equals(studentsCourses.getCourseName()))
        .collect(Collectors.toList());
  }
}