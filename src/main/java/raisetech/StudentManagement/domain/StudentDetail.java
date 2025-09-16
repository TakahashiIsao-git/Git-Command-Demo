package raisetech.StudentManagement.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.StudentManagement.data.CourseApplicationStatus;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

@Schema(description = "受講生詳細")
@Getter
@Setter
@NoArgsConstructor /** 引数を全く用いないコンストラクタ */
@AllArgsConstructor /** 全項目を持つコンストラクタ */
public class StudentDetail {

  @Valid
  private Student student;

  @Valid
  private List<StudentCourseDetail> studentCourseDetailList;
}
