package raisetech.StudentManagement.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;

@Getter
@Setter
public class StudentDetail {

  private String id;
  private String name;
  private String kanaName;
  private String nickName;
  private String email;
  private String area;
  private int age;
  private String sex;
  private String remark;
  private Boolean isDeleted;

  private Student student;

  public StudentDetail() {
    this.student = new Student();
  }
  private List<StudentsCourses> studentsCourses;
}