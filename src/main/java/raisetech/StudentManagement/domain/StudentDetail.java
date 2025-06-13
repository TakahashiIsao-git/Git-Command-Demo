package raisetech.StudentManagement.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

@Getter
@Setter
@NoArgsConstructor /** 引数を全く用いないコンストラクタ */
@AllArgsConstructor /** 全項目を持つコンストラクタ */
public class StudentDetail {

  private Student student;
  private List<StudentCourse> studentCourseList;
}
