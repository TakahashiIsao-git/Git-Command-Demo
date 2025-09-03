package raisetech.StudentManagement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.StudentManagement.data.CourseApplicationStatus;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentCourseDetail;
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

  // Eメールが空文字の場合のチェック
  @Test
  void 受講生詳細のEメールが空文字の時入力チェックに掛かること() {
    Student student = new Student();
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("");
    student.setArea("愛知");
    student.setSex("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);
    assertThat(violations).isNotEmpty();
    assertThat(violations).extracting("message").contains("メールは必須です。");
  }

  // 受講生IDが0の場合のチェック
  @Test
  void 受講生詳細のEメールが不正な形式の時入力チェックに掛かること() {
    Student student = new Student();
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("abc");
    student.setArea("愛知");
    student.setSex("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);
    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message").contains("正しいメールアドレスを入力してください。");
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
    studentDetail.setStudentCourseDetailList(List.of());
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
    // 受講生オブジェクトを作成
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

    // 受講生コースオブジェクトを作成
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(111);
    studentCourse.setStudentId(12L);
    studentCourse.setCourseName("データベース設計コース");
    studentCourse.setCourseStartAt(LocalDateTime.of(2025, 7, 1, 10, 0));
    studentCourse.setCourseEndAt(LocalDateTime.of(2025, 12, 31, 18, 0));

    // 申込状況オブジェクトを作成
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    courseApplicationStatus.setStudentCourseId(111);
    courseApplicationStatus.setApplicationStatus("本申込");
    courseApplicationStatus.setCreatedAt(LocalDateTime.of(2025, 8, 1, 13, 0));
    courseApplicationStatus.setLastUpdatedAt(LocalDateTime.of(2025, 8, 10, 18, 0));
    courseApplicationStatus.setLastUpdatedBy("admin");
    courseApplicationStatus.setNotes("本人確認済み");
    courseApplicationStatus.setIsDeleted(false);

    // 受講生コース詳細(申込状況付き)に受講生コースオブジェクトと申込状況オブジェクトを設定
    StudentCourseDetail studentCourseDetail = new StudentCourseDetail();
    studentCourseDetail.setStudentCourse(studentCourse);
    studentCourseDetail.setCourseApplicationStatus(courseApplicationStatus);

    // 受講生詳細に受講生と受講生コース詳細(申込状況付き)を設定
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseDetailList(List.of(studentCourseDetail));
    // any():引数は何でもOK
    Mockito.when(service.registerStudent(any())).thenReturn(studentDetail);

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
              "remark" : "備考欄",
              "isDeleted": false
            },
            "studentCourseDetailList" : [
             {
              "studentCourse": {
                "id" : 111,
                "studentId" : 12,
                "courseName" : "データベース設計コース",
                "courseStartAt" : "2025-07-01T10:00:00",
                "courseEndAt" : "2025-12-31T18:00:00"
               },
            "courseApplicationStatus": {
                "studentCourseId": 111,
                "applicationStatus": "本申込",
                "createdAt": "2025-08-01T13:00:00",
                "lastUpdatedAt": "2025-08-10T18:00:00",
                "lastUpdatedBy": "admin",
                "notes": "本人確認済み",
                "isDeleted": false
             }
            }
          ]
         }
        """
         */
        .andExpect(status().isOk());

    Mockito.verify(service, times(1)).registerStudent(any());
  }

  // PutMappingのREST APIのテスト StudentControllerクラスが@Valid付きのためダミーデータ作成
  @Test
  void 受講生情報の更新が実行できて空で返ってくること() throws Exception {
    // 受講生オブジェクトを作成
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

    // 受講生コースオブジェクトを作成
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(111);
    studentCourse.setStudentId(12L);
    studentCourse.setCourseName("データベース設計コース");
    studentCourse.setCourseStartAt(LocalDateTime.of(2025, 7, 1, 10, 0));
    studentCourse.setCourseEndAt(LocalDateTime.of(2025, 12, 31, 18, 0));

    // 申込状況オブジェクトを作成
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    courseApplicationStatus.setStudentCourseId(111);
    courseApplicationStatus.setApplicationStatus("受講中");
    courseApplicationStatus.setCreatedAt(LocalDateTime.of(2025, 8, 18, 9, 0));
    courseApplicationStatus.setLastUpdatedAt(LocalDateTime.of(2025, 8, 20, 13, 0));
    courseApplicationStatus.setLastUpdatedBy("system");
    courseApplicationStatus.setNotes("出席率90%・課題提出率95%");
    courseApplicationStatus.setIsDeleted(false);

    // 受講生コース詳細(申込状況付き)に受講生コースオブジェクトと申込状況オブジェクトを設定
    StudentCourseDetail studentCourseDetail = new StudentCourseDetail();
    studentCourseDetail.setStudentCourse(studentCourse);
    studentCourseDetail.setCourseApplicationStatus(courseApplicationStatus);

    // 受講生詳細に受講生と受講生コース詳細(申込状況付き)を設定
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseDetailList(List.of(studentCourseDetail));

    mockMvc.perform(MockMvcRequestBuilders.put("/updateStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        /* studentDetailの内容をJSON形式で表示
        """
          {
           "student": {
              "id" : 12,
              "name" : "テスト太郎",
              "kanaName" : "テストタロウ",
              "nickName" : "タロウ",
              "email" : "test@example.com",
              "area" : "愛知",
              "age" : 30,
              "sex" : "男性",
              "remark" : "備考欄",
              "isDeleted" : false
            },
            "studentCourseDetailList" : [
             {
              "studentCourse": {
                "id" : 111,
                "studentId" : 12,
                "courseName" : "データベース設計コース",
                "courseStartAt" : "2025-07-01T10:00:00",
                "courseEndAt" : "2025-12-31T18:00:00"
               },
            "courseApplicationStatus": {
                "studentCourseId": 111,
                "applicationStatus": "受講中",
                "createdAt": "2025-08-18T09:00:00",
                "lastUpdatedAt": "2025-08-20T13:00:00",
                "lastUpdatedBy": "system",
                "notes": "出席率90%・課題提出率95%",
                "isDeleted": false
             }
            }
          ]
         }
        """
         */
        .andExpect(status().isOk());
    // .andExpect(content().string("更新処理が成功しました。"));

    Mockito.verify(service, times(1)).updateStudent(any());
  }
  /*
  @Test
  void 受講生詳細の例外APIが実行できてステータスが400で返ってくること() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/exception"))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string("このAPIは現在利用できません。古いURLとなっています。"));
  }
   */

  // DeleteMappingのREST APIのテスト
  @Test
  void 任意の受講生IDが適切に論理削除できること() throws Exception {
    Long studentId = 15L;

    mockMvc.perform(MockMvcRequestBuilders.delete("/deleteStudent/{id}", studentId))
        .andExpect(status().isOk())
        .andExpect(content().string("受講生の論理削除が成功しました。"));

    Mockito.verify(service, times(1)).deleteStudent(studentId);
  }

  @Test
  void 論理削除時に受講生IDが数値以外の場合入力チェックに掛かること() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete("/deleteStudent/abc"))
        .andExpect(status().isBadRequest());

    // どんなIDでも一度もservice層が一切動かないこと
    Mockito.verify(service, never()).deleteStudent(any());
  }

  // PostMappingのREST APIのテスト
  @Test
  void 論理削除された受講生情報が適切に復元できること() throws Exception {
    Long id = 999L;

    mockMvc.perform(MockMvcRequestBuilders.post("/restoreStudent/{id}", id))
        .andExpect(status().isOk())
        .andExpect(content().string("受講生の復元が成功しました。"));

    Mockito.verify(service, times(1)).restoreStudent(id);
  }

  // GetMappingのREST APIのテスト
  @Test
  void 申込状況の一覧検索が実行できて空のリストが返ってくること() throws Exception {
    // モックの戻り値として空リストを設定
    Mockito.when(service.searchCourseApplicationStatusList()).thenReturn(List.of());
    // GETリクエストを実行し、申込状況とレスポンス内容を検証
    mockMvc.perform(MockMvcRequestBuilders.get("/courseApplicationStatusList"))
        .andExpect(status().isOk())
            .andExpect(content().json("[]")); // JSONレスポンスが空リストであること
    // Serviceメソッドの呼び出し回数を検証
    Mockito.verify(service, times(1)).searchCourseApplicationStatusList();
  }

  // GetMappingのREST APIのテスト
  @Test
  void 任意のIDに紐づく申込状況が取得できて異常が発生しないこと() throws Exception {
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    courseApplicationStatus.setId(1);
    courseApplicationStatus.setStudentCourseId(101);
    courseApplicationStatus.setApplicationStatus("仮申込");
    courseApplicationStatus.setIsDeleted(false);
    Mockito.when(service.searchCourseApplicationStatus(101)).thenReturn(courseApplicationStatus);

    mockMvc.perform(get("/courseApplicationStatus/{studentCourseId}", 101))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.applicationStatus").value("仮申込"));

    Mockito.verify(service, times(1)).searchCourseApplicationStatus(101);
  }

  // GetMappingのREST APIのテスト
  @Test
  void 申込状況詳細に存在しないIDを指定したときにNotFoundエラーが返ってくること() throws Exception {
    Mockito.when(service.searchCourseApplicationStatus(999)).thenReturn(null);

    mockMvc.perform(get("/courseApplicationStatus/999"))
        .andExpect(status().isNotFound());

    Mockito.verify(service).searchCourseApplicationStatus(999);
  }

  // PostMappingのREST APIのテスト
  @Test
  void 申込状況が適切に登録できること() throws Exception {
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    courseApplicationStatus.setStudentCourseId(103);
    courseApplicationStatus.setApplicationStatus("受講中");
    courseApplicationStatus.setIsDeleted(false);

    mockMvc.perform(MockMvcRequestBuilders.post("/registerCourseApplicationStatus")
          .contentType(MediaType.APPLICATION_JSON) //JSON形式
          .content(objectMapper.writeValueAsString(courseApplicationStatus))) //JavaのオブジェクトをJSONに変換して送信する
        .andExpect(status().isOk())
        .andExpect(content().string("登録処理が成功しました。"));

    // 回数保証と型指定の明示
    Mockito.verify(service, times(1))
        .registerCourseApplicationStatus(any(CourseApplicationStatus.class));
  }

  // PostMappingのREST APIのテスト
  @Test
  void 登録時に申込状況のステータスが空文字の時入力チェックに掛かること() throws Exception {
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    courseApplicationStatus.setStudentCourseId(105);
    courseApplicationStatus.setApplicationStatus("");
    courseApplicationStatus.setIsDeleted(false);

    mockMvc.perform(MockMvcRequestBuilders.post("/registerCourseApplicationStatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(courseApplicationStatus)))
        .andExpect(status().isBadRequest());

    // 不正な入力のときは、ServiceのregisterCourseApplicationStatusが一度も呼ばれていない
    Mockito.verify(service, never()).registerCourseApplicationStatus(any());
  }

  // PutMappingのREST APIのテスト
  @Test
  void 任意の申込状況が適切に更新できること() throws Exception {
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    courseApplicationStatus.setId(4);
    courseApplicationStatus.setStudentCourseId(104);
    courseApplicationStatus.setApplicationStatus("キャンセル");
    courseApplicationStatus.setIsDeleted(false);

    mockMvc.perform(MockMvcRequestBuilders.put("/updateCourseApplicationStatus/{studentCourseId}", 104)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(courseApplicationStatus)))
        .andExpect(status().isOk())
        .andExpect(content().string("更新処理が成功しました。"));

    Mockito.verify(service, times(1))
        .updateCourseApplicationStatus(eq(104), any());
  }

  // PutMappingのREST APIのテスト
  @Test
  void 更新時に申込状況のステータスが空文字の時入力チェックに掛かること() throws Exception {
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    courseApplicationStatus.setStudentCourseId(104);
    courseApplicationStatus.setApplicationStatus("");
    courseApplicationStatus.setIsDeleted(false);

    mockMvc.perform(MockMvcRequestBuilders.put("/updateCourseApplicationStatus/{studentCourseId}", 104)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(courseApplicationStatus)))
        .andExpect(status().isBadRequest());

    Mockito.verify(service, never()).updateCourseApplicationStatus(104, courseApplicationStatus);
  }

  // DeleteMappingのREST APIのテスト
  @Test
  void 任意のIDの申込状況を論理削除できること() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete("/deleteCourseApplicationStatus/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().string("申込状況情報の論理削除が成功しました。"));

    Mockito.verify(service, times(1)).deleteCourseApplicationStatus(1);
  }
}