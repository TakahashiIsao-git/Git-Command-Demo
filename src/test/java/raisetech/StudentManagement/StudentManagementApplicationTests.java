package raisetech.StudentManagement;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@RestController
public class StudentManagementApplicationTests {

	private Map<String, String>students = new HashMap<>();

	public StudentManagementApplicationTests() {
		students.put("TanakaKen", "25");
		students.put("TakahashiIsao", "50");
		students.put("SatouIori", "35");
	}
	
	public static void main(String[] args) {
		
		SpringBootTest.run(StudentManagementApplicationTests.class, args);
	}
	
	@GetMapping("/students")
	public Map<String, String>getStudents() {
		return students;
	}
	
	
	}
	
