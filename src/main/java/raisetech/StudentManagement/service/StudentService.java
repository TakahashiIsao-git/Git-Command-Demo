package raisetech.StudentManagement.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repositry.StudentRepository;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> searchStudentList() {
    return repository.search();
  }

  public StudentDetail searchStudent(String id) {
    Student student = repository.searchStudent(id);
    List<StudentsCourses> studentsCourses = repository.searchStudentsCourses(student.getId());
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentsCourses(studentsCourses);
    return studentDetail;
  }

  public List<StudentsCourses> searchStudentsCoursesList() {
    return repository.searchStudentsCoursesList();
  }

  @Transactional // 更新や削除などトランザクション管理のアノテーション（トランザクション範囲は任意）
  public void registerStudent(StudentDetail studentDetail) {
    // 受講生登録
    repository.registerStudent(studentDetail.getStudent());
    // TODO:コース情報登録
    for (StudentsCourses studentsCourse : studentDetail.getStudentsCourses()) {
      studentsCourse.setStudentId(Long.valueOf(String.valueOf(studentDetail.getStudent().getId())));
      studentsCourse.setCourseStartAt(LocalDateTime.now());
      studentsCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));
      repository.registerStudentsCourses(studentsCourse);
    }
  }
  // 受講生更新：repositoryから情報を受け取る
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    // 単一の受講生の更新
    repository.updateStudent(studentDetail.getStudent());
    // TODO:コース情報登録
    for (StudentsCourses studentsCourse : studentDetail.getStudentsCourses()) {
      repository.updateStudentsCourses(studentsCourse);
    }
  }
 }