package raisetech.StudentManagement.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

  @GetMapping("/studentsCourseList")
  public List<StudentsCourses> getStudentsCoursesList() {
    return service.searchStudentsCoursesList();
  }

  @GetMapping("/newStudent")
  public String newStudent(Model model) {
    model.addAttribute("studentDetail", new StudentDetail());
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
}