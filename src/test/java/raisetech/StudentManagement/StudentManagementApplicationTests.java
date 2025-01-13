package raisetech.StudentManagement;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class StudentManagementApplicationTests {

  private Map<String, String> students = new HashMap<>();

  public StudentManagementApplicationTests() {
    students.put("Tanaka Ken", "25");
    students.put("Takahashi Isao", "50");
    students.put("Satou Iori", "35");
  }

  public static void main(String[] args) {

    SpringApplication.run(StudentManagementApplicationTests.class, args);
  }

  @GetMapping("/students")
  public Map<String, String>getStudents() {
    return students;
  }

  @PostMapping("/students")
  public String addstudents(String name, String age) {
    students.put(name, age);
    return "Student added successfully";

  }
}

