package raisetech.StudentManagement.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.StudentManagement.data.CourseApplicationStatus;
import raisetech.StudentManagement.data.StudentCourse;

/**
 * StudentCourse（受講生のコース情報）と
 * CourseApplicationStatus（申込状況）を組み合わせて保持します。
 *
 *  このクラスを利用することで、受講生がどのコースを受講し、
 *  その申込状況がどうなっているかを一度に扱えます。
 */
@Schema(description = "受講生コース詳細(申込状況付き)")
@Getter
@Setter
@NoArgsConstructor/** 引数を全く用いないコンストラクタ */
@AllArgsConstructor/** 全項目を持つコンストラクタ */
public class StudentCourseDetail {

  /** 受講生のコース情報 */
  @Valid
  private StudentCourse studentCourse;

  /** コースの申込状況 */
  @Valid
  private CourseApplicationStatus courseApplicationStatus;
}
