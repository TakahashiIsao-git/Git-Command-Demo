package raisetech.StudentManagement.controller.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;

class StudentConverterTest {
  private StudentConverter sut;

  @BeforeEach
  void before() {
    sut = new StudentConverter();
  }

  @Test
  void 受講生のリストと受講生コース情報のリストを渡して受講生詳細のリストが作成できること() {
    // 事前準備：StudentConverterの中身の処理論理を検証するため具体的なデータを用意する
    Student student = createStudent(); // リファクタリングのメソッド抽出

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId(1L);
    studentCourse.setCourseName("Javaコース");
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

    List<Student> studentList = List.of(student);
    List<StudentCourse> studentCourseList = List.of(studentCourse);

    // 実行
    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList);

    // 検証
    assertThat(actual.get(0).getStudent()).isEqualTo(student);
    assertThat(actual.get(0).getStudentCourseList()).isEqualTo(studentCourseList);

    /*
    Student webStudent = new Student();
    webStudent.setId(2L);
    webStudent.setName("テスト一郎");

    StudentCourse javaStudentCourse = new StudentCourse();
    javaStudentCourse.setStudentId(1L);
    javaStudentCourse.setCourseName("Javaコース");

    StudentCourse webStudentCourse = new StudentCourse();
    webStudentCourse.setStudentId(2L);
    webStudentCourse.setCourseName("Web開発コース");

    // studentId=2Lの場合の検証
    // ２人目の受講生詳細データを取得する
    StudentDetail webStudentDetail = result.get(1);
    // webStudentDetailに紐づく受講生が適切にID=2Lに該当するか検証する
    assertThat(webStudentDetail.getStudent().getId()).isEqualTo(2L);
    // ２人目の受講生に受講生コースが１件あることを検証する
    assertThat(webStudentDetail.getStudentCourseList()).hasSize(1);
    // １件のみのコースが「Web開発コース」と適切なコースの名前となっているか検証する
    assertThat(webStudentDetail.getStudentCourseList().get(0).getCourseName()).isEqualTo("Web開発コース");*/
  }

  /*
  @Test
  void 値が入っていないリストを渡した時に空のリストの結果が適切に返ってくること() {
    // 事前準備
    List<Student> emptyStudentList = List.of();
    List<StudentCourse> emptyStudentCourseList = List.of();

    // 実行
    List<StudentDetail> result = studentConverter.convertStudentDetails(emptyStudentList, emptyStudentCourseList);

    // 検証：空であることを確認する
    assertThat(result).isEmpty();
  }
  */

  @Test
  void 受講生のリストと受講生コース情報のリストを渡した時に紐づかない受講生情報は除外されること() {
    // 事前準備
    Student student = createStudent();

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId(999L);
    studentCourse.setCourseName("Javaコース");
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

    List<Student> studentList = List.of(student);
    List<StudentCourse> studentCourseList = List.of(studentCourse);

    // 実行
    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList);

    // 検証
    assertThat(actual.get(0).getStudent()).isEqualTo(student);
    assertThat(actual.get(0).getStudentCourseList()).isEmpty();
    /*
    // 受講生が１人であることを検証する
    assertThat(result).hasSize(1);
    // 受講生詳細データを取得する
    StudentDetail studentDetail = result.get(0);
    // StudentDetailに紐づく受講生が適切にID=1Lに該当するか検証する
    assertThat(studentDetail.getStudent().getId()).isEqualTo(1L);
    // 受講生IDと受講生コースIDが紐づいていないため空の結果を返すか検証する
    assertThat(studentDetail.getStudentCourseList()).isEmpty();*/
  }

  private Student createStudent() {
    Student student = new Student();
    student.setId(1L);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("test@example.com");
    student.setArea("愛知");
    student.setAge(25);
    student.setSex("男性");
    student.setRemark("");
    student.setIsDeleted(false);
    return student;
  }
}
