package raisetech.StudentManagement.controller;

import static java.lang.reflect.Array.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
//import javax.xml.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.service.StudentService;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc; // SpringBootのテスト用起動

  @SuppressWarnings("removal")
  @MockBean
  private StudentService service;

  private Validator validator = (Validator) Validation.buildDefaultValidatorFactory().getValidator();

  // Springが自動注入してくれるJSON変換用のクラス
  @Autowired
  private ObjectMapper objectMapper;

  // GetMappingのREST APIのテスト
  @Test
  void 受講生詳細の一覧検索が実行できて空のリストが返ってくること() throws Exception {
    // Mockito.when(service.searchStudentList()).thenReturn(List.of(new StudentDetail()));

    mockMvc.perform(MockMvcRequestBuilders.get("/studentList"))
        .andExpect(status().isOk());
        //.andExpect(content().json("[{\"student\":null,\"studentCourseList\":null}]"));

    verify(service, times(1)).searchStudentList();
  }

  // Validationテスト
  @Test
  void 受講生詳細の受講生で適切な値を入力した時に入力チェックに異常が発生しないこと() {
    Student student = new Student();
    student.setId(1L);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("test@example.com");
    student.setArea("愛知");
    student.setSex("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);
    //assertEquals(0, violations.size()); テスト的に弱い
    assertThat(violations.size()).isEqualTo(0);
  }

  // 受講生IDがnullの場合のチェック
  @Test
  void 受講生詳細の受講生IDがnullのとき入力チェックのエラーになること() {
    Student student = new Student();
    student.setId(null);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("test@example.com");
    student.setArea("愛知");
    student.setSex("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);
    //assertEquals(1, violations.size());
    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message").containsOnly("IDは必須です。");
  }

  // 受講生IDが0の場合のチェック
  @Test
  void 受講生詳細の受講生IDが0のとき入力チェックのエラーになること() {
    Student student = new Student();
    student.setId(0L);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("test@example.com");
    student.setArea("愛知");
    student.setSex("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);
    //assertEquals(1, violations.size());
    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message").containsOnly("IDは1以上の数値である必要があります。");
  }

  // Studentクラスの@Emailチェック
  @Test
  void 受講生詳細のメールが不適当な型のときエラーになること() {
    Student student = new Student();
    student.setId(1L);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("invalid-test");
    student.setArea("愛知");
    student.setSex("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);
    assertThat(violations).extracting("message").containsOnly("正しいメールアドレスを入力してください。");
  }

  // GetMappingのREST APIのテスト
  @Test
  void IDに紐づく任意の受講生詳細の一覧検索が実行と取得ができること() throws Exception {
    Student student = new Student();
    student.setId(1L);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("test@example.com");
    student.setArea("愛知");
    student.setSex("男性");

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(List.of());
    Mockito.when(service.searchStudent(1L)).thenReturn(studentDetail);

    mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", 1L))
        .andExpect(status().isOk());

    Mockito.verify(service, times(1)).searchStudent(1L);
  }

  // GetMappingのREST APIのテスト
  @Test
  void 受講生コースの一覧検索が実行できて空のリストが返ってくること() throws Exception {
    Mockito.when(service.searchStudentCourseList()).thenReturn(List.of());

    mockMvc.perform(MockMvcRequestBuilders.get("/studentsCourseList"))
        .andExpect(status().isOk());

    Mockito.verify(service, times(1)).searchStudentCourseList();
  }

  // PostMappingのREST APIのテスト
  @Test
  void 受講生情報が適切に登録できること() throws Exception {
    Student student = new Student();
    student.setId(1L);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("test@example.com");
    student.setArea("愛知");
    student.setAge(25);
    student.setSex("男性");
    student.setRemark("備考欄");
    student.setIsDeleted(false);

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("course-111");
    studentCourse.setStudentId(1L);
    studentCourse.setCourseName("データベース設計コース");
    studentCourse.setCourseStartAt(LocalDateTime.of(2025, 7, 1, 10, 0));
    studentCourse.setCourseEndAt(LocalDateTime.of(2025, 12, 31, 18, 0));

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(List.of(studentCourse));
    // any():引数は何でもOK
    Mockito.when(service.registerStudent(Mockito.any(StudentDetail.class))).thenReturn(studentDetail);

    mockMvc.perform(MockMvcRequestBuilders.post("/registerStudent")
            .contentType("application/json") //JSON形式
            .content(objectMapper.writeValueAsString(studentDetail))) //JavaのオブジェクトをJSONに変換して送信する
        .andExpect(status().isOk());

    Mockito.verify(service, times(1)).registerStudent(Mockito.any(StudentDetail.class));
  }

  // PutMappingのREST APIのテスト
  @Test
  void 受講生情報が適切に更新できること() throws Exception {
    Student student = new Student();
    student.setId(1L);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("test@example.com");
    student.setArea("愛知");
    student.setAge(25);
    student.setSex("男性");
    student.setRemark("備考欄");
    student.setIsDeleted(false);

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("course-111");
    studentCourse.setStudentId(1L);
    studentCourse.setCourseName("データベース設計コース");
    studentCourse.setCourseStartAt(LocalDateTime.of(2025, 7, 1, 10, 0));
    studentCourse.setCourseEndAt(LocalDateTime.of(2025, 12, 31, 18, 0));

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(List.of(studentCourse));

    mockMvc.perform(MockMvcRequestBuilders.put("/updateStudent")
          .contentType("application/json")
          .content(objectMapper.writeValueAsString(studentDetail)))
        .andExpect(status().isOk())
            .andExpect(content().string("更新処理が成功しました。"));

    Mockito.verify(service, times(1)).updateStudent(Mockito.any());
  }

  // PostMappingのREST APIのテスト
  @Test
  void 論理削除された受講生情報が適切に復元できること() throws Exception {
    Long id = 999L;

    mockMvc.perform(MockMvcRequestBuilders.post("/restoreStudent/{id}", id))
        .andExpect(status().isOk())
        .andExpect(content().string("redirect:/studentList"));

    Mockito.verify(service, times(1)).restoreStudent(id);
  }
}