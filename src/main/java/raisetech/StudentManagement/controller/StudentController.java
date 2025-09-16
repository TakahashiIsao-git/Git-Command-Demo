package raisetech.StudentManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.data.CourseApplicationStatus;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.exception.TestException;
import raisetech.StudentManagement.service.StudentService;

/**
 *  受講生の検索、登録や更新などを行なうRSET APIとして受け付けるControllerです。(Javadocコメント)
 */
@Validated
@RestController
public class StudentController {

  private final StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  /**
   * 受講生詳細の一覧検索です。(誰に向けてのメモかを考える、チームメンバーならば敬語は不要)
   * 全件検索を行なうので、条件指定は行わないものになります。
   *
   * @return　受講生詳細一覧(全件)
   */
  @Operation(summary = "一覧検索", description = "受講生の一覧を検索します。") // summary:一覧 descrption:詳細
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList() throws TestException{
    return service.searchStudentList();
    // throw new TestException("現在このAPIは利用できません。URLは「studentList」ではなく「students」を利用してください。");
  }

  /**
   * 受講生詳細の検索です。
   * IDに紐づく任意の受講生の情報を取得します。
   *
   * @param id 受講生ID
   * @return　単一の受講生
   */
  @Operation(summary = "詳細検索", description = "IDに紐づく任意の受講生情報を取得します。")
  @GetMapping("/student/{id}")
  public StudentDetail getStudent(@PathVariable Long id) {
    return service.searchStudent(String.valueOf(id));
  }

  /**
   * 受講生コースの一覧検索です。
   * 全件検索を行なうので、条件指定は行わないものになります。
   *
   * @return 受講生コース一覧（全件）
   */
  @Operation(summary = "受講生コース一覧", description = "受講生のコース情報一覧を取得します。")
  @GetMapping("/studentsCourseList")
  public List<StudentCourse> getStudentsCoursesList() {
    return service.searchStudentCourseList();
  }

  /**
   * 受講生詳細の登録です。
   *
   * @param studentDetail 受講生詳細
   * @return 実行結果
   */
  @Operation(summary = "受講生登録", description = "受講生の情報を登録します。")
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(@RequestBody @Valid StudentDetail studentDetail) {
    StudentDetail responseStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }

  /**
   * 受講生詳細の更新です。
   * 名前(name)、 メール(email)、 コース情報などを更新します。
   * 論理削除フラグ(isDeleted)もリクエストに含まれていれば更新可能ですが、
   * 通常の論理削除は deleteStudentメソッドを使用してください。
   *
   * @param studentDetail 受講生詳細
   * @return 実行結果
   */
  @Operation(summary = "受講生更新" ,description = "受講生の情報を更新します。")
  @PutMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(@RequestBody @Valid StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました。");
  }

  /**
   * 受講生詳細の論理削除です(isDeletedフラグをtrueに設定)。
   *
   * @param  id 受講生ID
   * @return 実行結果
   */
  @Operation(summary = "受講生論理削除", description = "受講生の情報を論理削除します。")
  @DeleteMapping("/deleteStudent/{id}")
  public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
    service.deleteStudent(id);
    return ResponseEntity.ok("受講生の論理削除が成功しました。");
  }


  /**
   * 論理削除した受講生情報の復元を行ないます。
   *
   * @param id 受講生ID
   * @return 受講生一覧(全件)
   */
  @Operation(summary = "受講生復元", description = "論理削除された受講生情報を復元します。")
  @PostMapping("/restoreStudent/{id}")
  public ResponseEntity<String> restoreStudent(@PathVariable Long id) {
    service.restoreStudent(id);
    return ResponseEntity.ok("受講生の復元が成功しました。");
  }

  /**
   * 申込状況詳細の一覧検索です。
   * 全件検索を行なうので、条件指定は行わないものになります。
   *
   * @return 申込状況一覧（全件）
   */
  @Operation(summary = "申込状況一覧", description = "申込状況の情報一覧を取得します。")
  @GetMapping("/courseApplicationStatusList")
  public List<CourseApplicationStatus> getCourseApplicationStatusList() {
    return service.searchCourseApplicationStatusList();
  }

  /**
   * 申込状況詳細の検索です。
   * IDに紐づく任意の受講生の情報を取得します。
   *
   * @param studentCourseId 受講生コースID
   * @return 単一の申込状況
   */
  @Operation(summary = "詳細検索", description = "受講生コースIDに紐づく申込状況情報を取得します。")
  @GetMapping("/courseApplicationStatus/{studentCourseId}")
  public ResponseEntity<CourseApplicationStatus> getCourseApplicationStatus(@PathVariable Integer studentCourseId) {
    CourseApplicationStatus courseApplicationStatus = service.searchCourseApplicationStatus(studentCourseId);
    // 可読性とコードレビューのスムーズを考慮して==nullからObject.isNull()に修正
    if (Objects.isNull(courseApplicationStatus)) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(courseApplicationStatus);
  }

  /**
   * 申込状況詳細の登録です。
   *
   * @param courseApplicationStatus 申込状況
   * @return 実行結果
   */
  @Operation(summary = "申込状況登録", description = "申込状況の情報を登録します。")
  @PostMapping("/registerCourseApplicationStatus")
  public ResponseEntity<String> registerCourseApplicationStatus(
      @RequestBody @Valid CourseApplicationStatus courseApplicationStatus) {
    service.registerCourseApplicationStatus(courseApplicationStatus);
    return ResponseEntity.ok("登録処理が成功しました。");
  }

  /**
   * 申込状況詳細の更新です。
   *
   * @param studentCourseId 受講生コースID
   * @return 実行結果
   */
  @Operation(summary = "申込状況更新", description = "受講生コースIDに紐づく申込状況の情報を更新します。")
  @PutMapping("/updateCourseApplicationStatus/{studentCourseId}")
  public ResponseEntity<String> updateCourseApplicationStatus(
      @PathVariable Integer studentCourseId,
      @RequestBody @Valid CourseApplicationStatus courseApplicationStatus) {
    service.updateCourseApplicationStatus(studentCourseId, courseApplicationStatus);
    return ResponseEntity.ok("更新処理が成功しました。");
  }

  /**
   * 申込状況詳細の論理削除です(isDeletedフラグをtrueに設定)。
   *
   * @param id 申込状況ID
   * @return 実行結果
   */
  @Operation(summary = "申込状況の論理削除", description = "申込状況の情報を論理削除します。")
  @DeleteMapping("/deleteCourseApplicationStatus/{id}")
  public ResponseEntity<String> deleteCourseApplicationStatus(@PathVariable Integer id) {
    service.deleteCourseApplicationStatus(id);
    return ResponseEntity.ok("申込状況情報の論理削除が成功しました。");
  }
}
