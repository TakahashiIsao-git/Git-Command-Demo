package raisetech.StudentManagement.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repositry.StudentRepository;
import raisetech.StudentManagement.service.StudentService;

@RestController // ControllerからRestControllerに変換する,Webブラウザ上ではJSON変換した文字列を返すのみ
public class StudentController {

  private final StudentService service;
  private final StudentConverter converter;

  @Autowired //インジェクションする
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  @GetMapping("/studentList") /*HTTPのGETメソッドかつ[/studentList]のパスへのリクエストが
  メソッドにひもづけられる。*/
  public List<StudentDetail> getStudentList() { //String型からListに変更する、引数のModelを削除する
    List<Student> students = service.searchStudentList();
    List<StudentsCourses> studentsCourses = service.searchStudentsCoursesList();
    //コンバーターした結果を返す
    return converter.convertStudentDetails(students, studentsCourses);
  }

  @GetMapping("/student/{id}")
  public String getStudent(@PathVariable String id, Model model) {
    // 受講生情報を検索する
    StudentDetail studentDetail = service.searchStudent(id);
    // HTMLにデータを渡す
    model.addAttribute("studentDetail", studentDetail);
    // テンプレートファイルを特定するためのView名
    return "updateStudent";
  }

  @GetMapping("/studentsCourseList")
  public List<StudentsCourses> getStudentsCoursesList() {
    return service.searchStudentsCoursesList();
  }

  @GetMapping("/newStudent") //要素が空っぽの状態
  public String newStudent(Model model) {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudentsCourses(Arrays.asList(new StudentsCourses()));
    model.addAttribute("studentDetail", studentDetail);
    // テンプレートファイルを特定するためのView名
    return "registerStudent";
  }

  // 第15回演習課題：新規受講生情報を登録する処理を実装する。
  @PostMapping("/registerStudent")
  public String registerStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
    if (result.hasErrors()) {
      // 入力エラーが発生すれば登録ページへ戻る
      return "registerStudent";
    }
    // System.out.println(studentDetail.getStudent().getName() + "さんが新規受講生として登録されました。");
    service.registerStudent(studentDetail);
    // 登録後に受講生一覧ページへ
    return "redirect:/studentList";
  }

  // 第16回演習課題：updateStudentとしてstudentListにあるID情報を受け取り、検索の処理を行なう。（データが入っている状態なのでnewではない）
  @PostMapping("/updateStudent")
  // ①画面用@ModelAttributeから@RequestBodyに変換する ②画面用BindingResult resultを削除する
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    // 受講生情報を更新する
    service.updateStudent(studentDetail);
    // 返す内容がbodyに必要
    return ResponseEntity.ok("更新処理が成功しました。");
  }

  // 論理削除した受講生情報を復元する処理
  @PostMapping("/restoreStudent/{id}")
  public String restoreStudent(@PathVariable Long id) {
    service.restoreStudent(id);
    return "redirect:/studentList";
  }
}