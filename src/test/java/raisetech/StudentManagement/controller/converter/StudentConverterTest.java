package raisetech.StudentManagement.controller.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raisetech.StudentManagement.data.CourseApplicationStatus;
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
  void 受講生のリストと受講生コース情報のリストと申込状況情報のリストを渡して受講生詳細のリストが作成できること() {
    // 事前準備：StudentConverterの中身の処理論理を検証するため具体的なデータを用意する
    Student student = createStudent(); // リファクタリングのメソッド抽出

    // 受講生コースのテストデータの用意
    StudentCourse studentCourse = new StudentCourse(
    1L,
    "Javaコース",
    LocalDateTime.now(),
    LocalDateTime.now().plusYears(1)
    );
    studentCourse.setId(101);

    // 申込状況のテストデータの用意
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus(
    101,
    "仮申込",
    LocalDateTime.now(),
    LocalDateTime.now(),
    "admin",
    "初回問い合わせあり",
    false
    );
    courseApplicationStatus.setId(1);

    // 実行
    List<StudentDetail> actual = sut.convertStudentDetails(
        List.of(student), List.of(studentCourse), List.of(courseApplicationStatus)
    );

    // 検証
    assertThat(actual.get(0).getStudent()).isEqualTo(student);
    assertThat(actual.get(0).getStudentCourseDetailList().get(0).getStudentCourse())
        .isEqualTo(studentCourse);
    assertThat(actual.get(0).getStudentCourseDetailList().get(0).getCourseApplicationStatus())
        .isEqualTo(courseApplicationStatus);
  }

  @Test
  void 申込状況が存在しない受講生情報はnullが設定されること() {
    // 事前準備
    Student student = createStudent();

    StudentCourse studentCourse = new StudentCourse(
    1L,
    "Javaコース",
    LocalDateTime.now(),
    LocalDateTime.now().plusYears(1)
    );
    studentCourse.setId(101);

    // 申込状況が空のテストデータを設定する
    List<CourseApplicationStatus> courseApplicationStatusList = List.of();

    // 実行
    List<StudentDetail> actual = sut.convertStudentDetails(
        List.of(student), List.of(studentCourse), courseApplicationStatusList
    );

    // 検証
    assertThat(actual.get(0).getStudentCourseDetailList().get(0).getCourseApplicationStatus()).isNull();
  }

  private Student createStudent() {
    Student student = new Student(
    "テスト太郎",
    "テストタロウ",
    "テスト",
    "test@example.com",
    "愛知",
    25,
    "男性",
    "",
    false
    );
    student.setId(1L);
    return student;
  }
}
