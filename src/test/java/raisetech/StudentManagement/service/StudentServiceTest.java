package raisetech.StudentManagement.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.CourseApplicationStatus;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentCourseDetail;
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
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<CourseApplicationStatus> courseApplicationStatusList = new ArrayList<>();

    Mockito.when(repository.search()).thenReturn(studentList);
    Mockito.when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    Mockito.when(repository.searchCourseApplicationStatusList()).thenReturn(courseApplicationStatusList);
    Mockito.when(converter.convertStudentDetails(studentList, studentCourseList, courseApplicationStatusList))
        .thenReturn(new ArrayList<>());

    // 実行
    sut.searchStudentList(); // actual:テスト検証対象

    // 検証
    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(converter, times(1)).convertStudentDetails(
        studentList, studentCourseList, courseApplicationStatusList);

    //　後処理
  }

  @Test
  void 受講生詳細検索_IDに紐づきリポジトリの処理が適切に呼び出せていること() {
    // 事前準備(before)
    Long id = 999L;
    Student student = new Student();
    student.setId(id);
    StudentDetail expected = new StudentDetail(student, new ArrayList<>());

    Mockito.when(repository.searchStudent(id)).thenReturn(student);
    Mockito.when(repository.searchStudentCourse(student.getId())).thenReturn(new ArrayList<>());
    //
    Mockito.when(converter.convertStudentDetails(Mockito.anyList(), Mockito.anyList(), Mockito.anyList()))
        .thenReturn(List.of(expected));

    // 実行
    StudentDetail actual = sut.searchStudent("999");

    // 検証
    Mockito.verify(repository, times(1)).searchStudent(id);
    Mockito.verify(repository, times(1)).searchStudentCourse(id);
    Mockito.verify(converter).convertStudentDetails(Mockito.anyList(), Mockito.anyList(), Mockito.anyList());
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
    // 受講生コース詳細（申込状況付き）を作成
    StudentCourse studentCourse = new StudentCourse();
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    StudentCourseDetail studentCourseDetail = new StudentCourseDetail(studentCourse, courseApplicationStatus);
    // 作成した受講生コース詳細（申込状況付き）をリスト化
    List<StudentCourseDetail> studentCourseDetailList = List.of(studentCourseDetail);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseDetailList);

    // 実行
    sut.registerStudent(studentDetail);

    // 検証
    Mockito.verify(repository, times(1)).registerStudent(student);
    Mockito.verify(repository, times(1)).registerStudentCourse(studentCourse);
    Mockito.verify(repository, times(1)).registerCourseApplicationStatus
        (Mockito.any(CourseApplicationStatus.class));
  }

  @Test
  void 受講生詳細の登録_初期化処理が適切に行なわれること() {
    // 事前準備(before)
    Student student = new Student();
    student.setId(999L);
    StudentCourse studentCourse = new StudentCourse();
    // ミリ秒単位のズレによるテスト失敗を防ぐため設定
    LocalDateTime now = LocalDateTime.now();

    // 実行
    sut.initStudentCourse(studentCourse, student);

    // 検証
    Assertions.assertEquals(999L, studentCourse.getStudentId());
    Assertions.assertEquals(now.getHour(), studentCourse.getCourseStartAt().getHour());
    Assertions.assertEquals(now.plusYears(1).getYear(), studentCourse.getCourseEndAt().getYear());
  }

  @Test
  void 受講生更新処理_リポジトリの更新メソッドが適切に呼び出せていること() {
    // 事前準備(before)
    Student student = new Student();
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(101);
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    StudentCourseDetail studentCourseDetail = new StudentCourseDetail(studentCourse, courseApplicationStatus);
    List<StudentCourseDetail> studentCourseDetailList = List.of(studentCourseDetail);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseDetailList);

    // 実行
    sut.updateStudent(studentDetail);

    // 検証
    Mockito.verify(repository, times(1)).updateStudent(student);
    Mockito.verify(repository, times(1)).updateStudentCourse(studentCourse);
    Mockito.verify(repository, times(1)).updateCourseApplicationStatus(
        studentCourse.getId(), courseApplicationStatus);
  }

  @Test
  void 受講生論理削除処理_リポジトリの処理が適切に呼び出せていること() {
    // 事前準備
    Long studentId = 15L;

    // 実行
    sut.deleteStudent(studentId);

    // 検証
    Mockito.verify(repository, times(1)).deleteStudent(studentId);
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

  @Test
  void 申込状況一覧検索_リポジトリの処理が適切に呼び出せていること() {
    // 事前準備
    List<CourseApplicationStatus> expectedList = new ArrayList<>();
    Mockito.when(repository.searchCourseApplicationStatusList()).thenReturn(expectedList);

    // 実行
    List<CourseApplicationStatus> actual = sut.searchCourseApplicationStatusList();

    // 検証
    Mockito.verify(repository, times(1)).searchCourseApplicationStatusList();
    Assertions.assertEquals(expectedList, actual);
  }

  @Test
  void 申込状況検索_IDに紐づきリポジトリの処理が適切に呼び出せていること() {
    // 事前準備
    Integer studentCourseId = 101;
    CourseApplicationStatus expected = new CourseApplicationStatus();
    Mockito.when(repository.searchCourseApplicationStatus(studentCourseId)).thenReturn(expected);

    // 実行
    CourseApplicationStatus actual = sut.searchCourseApplicationStatus(studentCourseId);
    // 検証
    Mockito.verify(repository, times(1)).searchCourseApplicationStatus(studentCourseId);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void 申込状況登録処理_リポジトリの処理が適切に呼び出せていること() {
    // 事前準備
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();

    // 実行
    sut.registerCourseApplicationStatus(courseApplicationStatus);

    // 検証
    Mockito.verify(repository, times(1))
        .registerCourseApplicationStatus(courseApplicationStatus);
  }

  @Test
  void 申込状況更新処理_リポジトリの更新メソッドが適切に呼び出せていること() {
    // 事前準備
    Integer studentCourseId = 101;
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();

    // 実行
    sut.updateCourseApplicationStatus(studentCourseId, courseApplicationStatus);

    // 検証
    Mockito.verify(repository, times(1))
        .updateCourseApplicationStatus(studentCourseId, courseApplicationStatus);
  }

  @Test
  void 申込状況論理削除処理_リポジトリの論理削除メソッドが適切に呼び出せていること() {
    // 事前準備
    Integer id = 1;

    // 実行
    sut.deleteCourseApplicationStatus(id);

    // 検証
    Mockito.verify(repository, times(1)).deleteCourseApplicationStatus(id);
  }
}
