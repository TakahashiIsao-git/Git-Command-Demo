package raisetech.StudentManagement.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach // methodごと
  void before() {
    sut = new StudentService(repository, converter);  // sut:serviceのテスト対象
  }

  @Test
  void 受講生詳細一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること() {
    // 事前準備(before) Serviceのエラー対策：Mock化（Mockito）
    /* StudentService sut = new StudentService(repository, converter);
    // List<StudentDetail> expected = new ArrayList<>(); // expected:テスト結果の予想 */
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();

    Mockito.when(repository.search()).thenReturn(studentList);
    Mockito.when(repository.searchStudentCourseList()).thenReturn(studentCourseList);

    // 実行
    sut.searchStudentList(); // actual:テスト検証対象

    // 検証
    // Assertions.assertEquals(expected, actual); //expected = actual
    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);

    //　後処理
  }

  @Test
  void 受講生詳細検索_IDに紐づきリポジトリの処理が適切に呼び出せていること() {
    // 事前準備(before)
    Long id = 999L;
    Student student = new Student();
    student.setId(id);
    //List<StudentCourse> courseList = new ArrayList<>();

    Mockito.when(repository.searchStudent(id)).thenReturn(student);
    Mockito.when(repository.searchStudentCourse(student.getId())).thenReturn(new ArrayList<>());

    // 実行
    StudentDetail expected = new StudentDetail(student, new ArrayList<>());
    StudentDetail actual = sut.searchStudent("999");
    // 検証
    Mockito.verify(repository, times(1)).searchStudent(id);
    Mockito.verify(repository, times(1)).searchStudentCourse(id);
    Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
  }

  @Test
  void 受講生コース一覧検索_リポジトリの処理が適切に呼び出せていること() {
    // 事前準備(before)
    List<StudentCourse> expectedList = new ArrayList<>();
    Mockito.when(repository.searchStudentCourseList()).thenReturn(expectedList);

    // 実行
    List<StudentCourse> actual = sut.searchStudentCourseList();

    // 検証
    verify(repository, times(1)).searchStudentCourseList();
    Assertions.assertEquals(expectedList, actual);
  }

  @Test
  void 受講生登録処理_リポジトリの処理が適切に呼び出せていること() {
    // 事前準備(before)
    Student student = new Student();
    student.setId(999L);
    StudentCourse studentCourse = new StudentCourse();
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseList);

    // 実行
    sut.registerStudent(studentDetail);

    // 検証
    Mockito.verify(repository, times(1)).registerStudent(student);
    Mockito.verify(repository, times(1)).registerStudentCourse(studentCourse);
  }

  @Test
  void 受講生詳細の登録_初期化処理が適切に行なわれること() {
    // 事前準備(before)
    Student student = new Student();
    student.setId(999L);
    StudentCourse studentCourse = new StudentCourse();

    // 実行
    sut.initStudentCourse(studentCourse, student);

    // 検証
    Assertions.assertEquals(999L, studentCourse.getStudentId());
    Assertions.assertEquals(LocalDateTime.now().getHour(), studentCourse.getCourseStartAt().getHour());
    Assertions.assertEquals(LocalDateTime.now().plusYears(1).getYear(), studentCourse.getCourseEndAt().getYear());
  }

  @Test
  void 受講生更新処理_リポジトリの更新メソッドが適切に呼び出せていること() {
    // 事前準備(before)
    Student student = new Student();
    StudentCourse studentCourse = new StudentCourse();
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseList);

    // 実行
    sut.updateStudent(studentDetail);

    // 検証
    Mockito.verify(repository, times(1)).updateStudent(student);
    Mockito.verify(repository, times(1)).updateStudentCourse(studentCourse);
  }

  @Test
  void 受講生復元処理_リポジトリの処理が適切に呼び出せていること() {
    // 事前準備(before)
    Long studentId = 99L;

    // 実行
    sut.restoreStudent(studentId);

    // 検証
    Mockito.verify(repository, times(1)).restoreStudent(studentId);
  }
}