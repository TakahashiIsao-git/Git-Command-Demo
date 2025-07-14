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
import org.springframework.http.MediaType;
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
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

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
  void 受講生詳細の受講生IDがnullの時入力チェックに掛かること() {
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
  void 受講生詳細の受講生IDが0の時入力チェックに掛かること() {
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
  void 受講生詳細のメールが不適当な型の時に入力チェックに掛かること() {
    Student student = new Student();
    student.setId(12L);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("invalid-test");
    student.setArea("愛知");
    student.setSex("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);
    assertThat(violations).extracting("message").containsOnly("正しいメールアドレスを入力してください。");
  }

  // GetMappingのREST APIのテスト Validationテストを含む
  @Test
  void IDに紐づく任意の受講生詳細の一覧検索が実行と取得ができること() throws Exception {
    Student student = new Student();
    student.setId(12L);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("test@example.com");
    student.setArea("愛知");
    student.setSex("男性");
    // REST API用テストデータ　例：Long id = 999L;

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(List.of());
    Mockito.when(service.searchStudent("12")).thenReturn(studentDetail); // idから1Lに変更

    mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", 12L))
        .andExpect(status().isOk());

    Mockito.verify(service, times(1)).searchStudent("12");
  }

  // GetMappingのREST APIのテスト
  @Test
  void 受講生コースの一覧検索が実行できて空のリストが返ってくること() throws Exception {
    Mockito.when(service.searchStudentCourseList()).thenReturn(List.of());

    mockMvc.perform(MockMvcRequestBuilders.get("/studentsCourseList"))
        .andExpect(status().isOk());

    Mockito.verify(service, times(1)).searchStudentCourseList();
  }

  /* PostMappingのREST APIのテスト StudentControllerクラスが@Valid付きのためダミーデータ作成
     リクエストデータは適切に構築して入力チェックの検証も兼ねている。
     本来であれば帰りは登録されたデータが入るが、モック化すると意味がないためレスポンスは作らない。
   */
  @Test
  void 受講生詳細の登録が実行できて空で返ってくること() throws Exception {
    Student student = new Student();
    student.setId(12L);
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
    studentCourse.setStudentId(12L);
    studentCourse.setCourseName("データベース設計コース");
    studentCourse.setCourseStartAt(LocalDateTime.of(2025, 7, 1, 10, 0));
    studentCourse.setCourseEndAt(LocalDateTime.of(2025, 12, 31, 18, 0));

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(List.of(studentCourse));
    // any():引数は何でもOK
    Mockito.when(service.registerStudent(Mockito.any())).thenReturn(studentDetail);

    mockMvc.perform(MockMvcRequestBuilders.post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON) //JSON形式
            .content(objectMapper.writeValueAsString(studentDetail))) //JavaのオブジェクトをJSONに変換して送信する
        /* Postman実行前にテスト実行後返ってくるであろうデータ形式を作成する。
        """
          {
            "student": {
              "name" : "テスト太郎",
              "kanaName" : "テストタロウ",
              "nickName" : "テスト",
              "email" : "test@example.com",
              "area" : "愛知",
              "age" : 25,
              "sex" : "男性",
              "remark" : ""
            },
            "studentCourseList" : [
              {
                "courseName" : "Javaコース"
              }
            ]
           }
        """
         */
        .andExpect(status().isOk());

    Mockito.verify(service, times(1)).registerStudent(Mockito.any());
  }

  // PutMappingのREST APIのテスト StudentControllerクラスが@Valid付きのためダミーデータ作成
  @Test
  void 受講生情報の更新が実行できて空で返ってくること() throws Exception {
    Student student = new Student();
    student.setId(12L);
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
    studentCourse.setStudentId(12L);
    studentCourse.setCourseName("データベース設計コース");
    studentCourse.setCourseStartAt(LocalDateTime.of(2025, 7, 1, 10, 0));
    studentCourse.setCourseEndAt(LocalDateTime.of(2025, 12, 31, 18, 0));

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(List.of(studentCourse));

    mockMvc.perform(MockMvcRequestBuilders.put("/updateStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        /* studentDetailの内容をJSON形式で表示
        """
          {
           "student": {
              "id" : "12L",
              "name" : "テスト太郎",
              "kanaName" : "テストタロウ",
              "nickName" : "タロウ",
              "email" : "test@example.com",
              "area" : "愛知",
              "age" : 30,
              "sex" : "男性",
              "remark" : ""
            },
            "studentCourseList" : [
              {
                "id" : "13",
                "studentId" : 12L,
                "courseName" : "Javaコース",
                "courseStartAt" : "2025-07-01T10:00:00",
                "courseEndAt" : "2025-12-31T18:00:00"
              }
            ]
          }
        """
         */
        .andExpect(status().isOk());
    // .andExpect(content().string("更新処理が成功しました。"));

    Mockito.verify(service, times(1)).updateStudent(Mockito.any());
  }
  /*
  @Test
  void 受講生詳細の例外APIが実行できてステータスが400で返ってくること() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/exception"))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string("このAPIは現在利用できません。古いURLとなっています。"));
  }
   */

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