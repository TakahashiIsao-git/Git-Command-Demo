package raisetech.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の全件検索が実行できること() {
    List<Student> actual = sut.search();

    Assertions.assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void 任意の受講生IDに紐づく受講生検索が実行できること() {
    Student actual = sut.searchStudent(1L);

    /* データの内容の確認
    System.out.println(actual); */
    assertThat(actual.getName()).isEqualTo("山田太郎");
    assertThat(actual.getKanaName()).isEqualTo("ヤマダタロウ");
    assertThat(actual.getNickName()).isEqualTo("タロ");
    assertThat(actual.getEmail()).isEqualTo("taro@example.com");
    assertThat(actual.getArea()).isEqualTo("東京");
    assertThat(actual.getAge()).isEqualTo(25);
    assertThat(actual.getSex()).isEqualTo("男性");
  }

  @Test
  void 受講生コース情報の全件検索が実行できること() {
    List<StudentCourse> actual = sut.searchStudentCourseList();

    /* データの内容の確認
    System.out.println(actual); */
    assertThat(actual.size()).isEqualTo(9);
  }

  @Test
  void 任意の受講生IDに紐づく受講生コースの検索が実行できること() {
    List<StudentCourse> actual = sut.searchStudentCourse(3L);

    /* データの内容の確認
    System.out.println(actual); */
    assertThat(actual.size()).isEqualTo(3);
    assertThat(actual.get(0).getCourseName()).isEqualTo("Javaコース");
    assertThat(actual.get(1).getCourseName()).isEqualTo("AI・機械学習コース");
    assertThat(actual.get(2).getCourseName()).isEqualTo("ネットワークコース");
  }

  @Test
  void 受講生の登録が実行できること() {
    Student student = new Student();
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("test@example.com");
    student.setArea("愛知");
    student.setAge(25);
    student.setSex("男性");
    student.setRemark("");
    student.setIsDeleted(false);

    sut.registerStudent(student);

    List<Student> actual = sut.search();

    /* データの内容の確認
    System.out.println(actual); */
    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生コース情報の登録が実行できること() {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(1L);
    studentCourse.setCourseName("セキュリティコース");
    studentCourse.setCourseStartAt(LocalDateTime.of(2025, 6, 1, 9, 0, 0));
    studentCourse.setCourseEndAt(LocalDateTime.of(2025, 10, 1, 17, 0, 0));

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourse(1L);

    /* データの内容の確認
    System.out.println(actual); */
    assertThat(actual.size()).isEqualTo(3);
    assertThat(actual.get(2).getCourseName()).isEqualTo("セキュリティコース");
  }

  @Test
  void 受講生の更新が実行できること() {
    Student student = sut.searchStudent(4L);
    student.setArea("長崎");
    student.setAge(24);

    sut.updateStudent(student);

    Student actual = sut.searchStudent(4L);

    /* データの更新内容の確認
    System.out.println(actual); */
    assertThat(actual.getArea()).isEqualTo("長崎");
    assertThat(actual.getAge()).isEqualTo(24);
  }

  @Test
  void 受講生コース情報のコース名の更新が実行できること() {
    List<StudentCourse> studentCourses = sut.searchStudentCourse(5L);
    StudentCourse studentCourse = studentCourses.get(0);

    studentCourse.setCourseName("改訂版ネットワークコース");
    sut.updateStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourse(5L);

    /* データの更新内容の確認
    System.out.println(actual); */
    assertThat(actual.get(0).getCourseName()).isEqualTo("改訂版ネットワークコース");
  }

  @Test
  void 論理削除された受講生の復元が実行できること() {
    Student student = sut.searchStudent(2L);
    student.setIsDeleted(true);
    sut.updateStudent(student);

    sut.restoreStudent(2L);

    Student actual = sut.searchStudent(2L);
    /* データの内容の確認
    System.out.println(actual); */
    assertThat(actual.getIsDeleted()).isFalse();
  }
}