package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 受講生コース情報を扱うオブジェクト。
 */
@Schema(description = "受講生コース情報")
@Getter
@Setter
public class StudentCourse {

  private int id;
  private Long studentId;

  @NotBlank
  private String courseName;

  @NotNull
  private LocalDateTime courseStartAt;

  @NotNull
  private LocalDateTime courseEndAt;

  /** 引数付きコンストラクタ 本番用(idなし) */
  public StudentCourse(
      Long studentId,
      String courseName,
      LocalDateTime courseStartAt,
      LocalDateTime courseEndAt
  ) {
    this.studentId = studentId;
    this.courseName = courseName;
    this.courseStartAt = courseStartAt;
    this.courseEndAt = courseEndAt;
  }

  /** 引数付きコンストラクタ テスト専用(idあり) */
  public StudentCourse(int id, Long studentId, String courseName,
      LocalDateTime courseStartAt, LocalDateTime courseEndAt) {
    this(studentId, courseName, courseStartAt, courseEndAt);
    this.id = id;
  }

  /** JPA用にデフォルトコンストラクタ */
  public StudentCourse() {}

}
