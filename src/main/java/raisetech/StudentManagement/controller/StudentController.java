package raisetech.StudentManagement.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.service.StudentService;

@RestController
public class StudentController {

  private final StudentService service;
  private final StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  // 全件検索
  @GetMapping("/studentList")
  public List<Student> getStudentList() {
    List<Student> students = service.searchStudentList();
    return students;
  }

  // 年代を30代に限定して絞り込み検索
  @GetMapping("/30yearsOldStudentList")
  public List<Student> get30yearsOldStudentList() {
    /* Model model
    List<Student> students = service.searchStudentList();
    List<StudentsCourses> studentsCourses = service.searchStudentsCoursesList();
    model.addAttribute("studentList", converter.convertStudentDetails(students, studentsCourses)); */
    return service.search30yearsOldStudentList();
  }

  @GetMapping("/studentsCoursesList")
  public List<StudentsCourses> getStudentcoursesList() {
    return service.searchStudentsCoursesList();
  }
}
