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
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(101);
    studentCourse.setStudentId(1L);
    studentCourse.setCourseName("Javaコース");
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

    // 申込状況のテストデータの用意
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    courseApplicationStatus.setId(1);
    courseApplicationStatus.setStudentCourseId(101);
    courseApplicationStatus.setApplicationStatus("仮申込");
    courseApplicationStatus.setCreatedAt(LocalDateTime.now());
    courseApplicationStatus.setLastUpdatedAt(LocalDateTime.now());
    courseApplicationStatus.setLastUpdatedBy("admin");
    courseApplicationStatus.setNotes("初回問い合わせあり");

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

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(101);
    studentCourse.setStudentId(1L);
    studentCourse.setCourseName("Javaコース");
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

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
