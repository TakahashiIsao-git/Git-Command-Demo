package raisetech.StudentManagement.controller.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;

class StudentConverterTest {
  private StudentConverter studentConverter = new StudentConverter();

  @Test
  void 受講生情報と受講生コース情報が適切にマッピングできること() {
    // 事前準備：StudentConverterの中身の処理論理を検証するため具体的なデータを用意する
    Student javaStudent = new Student();
    javaStudent.setId(1L);
    javaStudent.setName("テスト太郎");

    Student webStudent = new Student();
    webStudent.setId(2L);
    webStudent.setName("テスト一郎");

    StudentCourse aiStudentCourse = new StudentCourse();
    aiStudentCourse.setStudentId(1L);
    aiStudentCourse.setCourseName("AI・機械学習コース");
    aiStudentCourse.setCourseStartAt(LocalDateTime.of(2025, 7, 1, 10, 0));
    aiStudentCourse.setCourseEndAt(LocalDateTime.of(2025, 12, 31, 18, 0));

    StudentCourse javaStudentCourse = new StudentCourse();
    javaStudentCourse.setStudentId(1L);
    javaStudentCourse.setCourseName("Javaコース");

    StudentCourse webStudentCourse = new StudentCourse();
    webStudentCourse.setStudentId(2L);
    webStudentCourse.setCourseName("Web開発コース");

    List<Student> studentList = List.of(javaStudent, webStudent);
    List<StudentCourse> studentCourseList = List.of(aiStudentCourse, javaStudentCourse, webStudentCourse);

    // 実行
    List<StudentDetail> result = studentConverter.convertStudentDetails(studentList, studentCourseList);

    // 検証
    // ２人分の受講生情報（StudentDetail）があるか検証
    assertThat(result).hasSize(2);

    // studentId=1Lの場合の検証
    // １人目の受講生詳細データを取得する
    StudentDetail javaStudentDetail = result.get(0);
    // javaStudentDetailに紐づく受講生が適切にID=1Lに該当するか検証する
    assertThat(javaStudentDetail.getStudent().getId()).isEqualTo(1L);
    // １人目の受講生に受講生コースが２件あることを検証する
    assertThat(javaStudentDetail.getStudentCourseList()).hasSize(2);

    // studentId=2Lの場合の検証
    // ２人目の受講生詳細データを取得する
    StudentDetail webStudentDetail = result.get(1);
    // webStudentDetailに紐づく受講生が適切にID=2Lに該当するか検証する
    assertThat(webStudentDetail.getStudent().getId()).isEqualTo(2L);
    // ２人目の受講生に受講生コースが１件あることを検証する
    assertThat(webStudentDetail.getStudentCourseList()).hasSize(1);
    // １件のみのコースが「Web開発コース」と適切なコースの名前となっているか検証する
    assertThat(webStudentDetail.getStudentCourseList().get(0).getCourseName()).isEqualTo("Web開発コース");
  }

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

  @Test
  void 受講生と受講生コースIDが一致しない時に受講生コースが紐づかないこと() {
    // 事前準備
    Student student = new Student();
    student.setId(1L);

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(999L);

    List<Student> studentList = List.of(student);
    List<StudentCourse> studentCourseList = List.of(studentCourse);

    // 実行
    List<StudentDetail> result = studentConverter.convertStudentDetails(studentList, studentCourseList);

    // 検証
    // 受講生が１人であることを検証する
    assertThat(result).hasSize(1);
    // 受講生詳細データを取得する
    StudentDetail studentDetail = result.get(0);
    // StudentDetailに紐づく受講生が適切にID=1Lに該当するか検証する
    assertThat(studentDetail.getStudent().getId()).isEqualTo(1L);
    // 受講生IDと受講生コースIDが紐づいていないため空の結果を返すか検証する
    assertThat(studentDetail.getStudentCourseList()).isEmpty();
  }
}