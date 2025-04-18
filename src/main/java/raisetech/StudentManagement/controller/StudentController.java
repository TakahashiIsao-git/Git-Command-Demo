package raisetech.StudentManagement.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repositry.StudentRepository;
import raisetech.StudentManagement.service.StudentService;

@Controller /*①コンポーネントスキャンされてBeanとして管理される。
②SpringMVCに認知してもらい様々なアノテーションを利用できる。*/
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
  public String getStudentList(Model model) { //ModelはSpringMVCが提供する型で,Viewに参照してもらうオブジェクトを格納できる。
    List<Student> students = service.searchStudentList();
    List<StudentsCourses> studentsCourses = service.searchStudentsCoursesList();

    model.addAttribute("studentList", converter.convertStudentDetails(students, studentsCourses));
    return "studentList"; //テンプレートファイルを特定するためのView名
  }

  @GetMapping("/student/{id}")
  public String getStudent(@PathVariable String id, Model model) {
    // 受講生情報を検索する
    StudentDetail studentDetail = service.searchStudent(id);
    // HTMLにデータを渡す
    model.addAttribute("studentDetail", studentDetail);
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
    return "registerStudent"; //View名
  }

  @PostMapping("/registerStudent")
  public String registerStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
    if (result.hasErrors()) {
      return "registerStudent";
    }
    // System.out.println(studentDetail.getStudent().getName() + "さんが新規受講生として登録されました。");
    // 第15回演習課題：①新規受講生情報を登録する処理を実装する。
    service.registerStudent(studentDetail);
    // ②コース情報も一緒に登録できるよう実装する（コースは単体で可）。
    return "redirect:/studentList";
  }

  // 第16回演習課題：updateStudentとしてstudentListにあるID情報を受け取り、検索の処理を行なう。（データが入っている状態なのでnewではない）
  @PostMapping("/updateStudent")
  public String updateStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
    if (result.hasErrors()) {
      // 入力エラーが発生すれば更新ページへ戻る
      return "updateStudent";
    }
    // 受講生情報を更新する
    service.updateStudent(studentDetail);
    // 更新後に受講生一覧ページへ
    return "redirect:/studentList";
  }
}