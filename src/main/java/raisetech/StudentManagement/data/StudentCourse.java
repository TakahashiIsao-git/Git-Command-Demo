package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
// import lombok.ToString;

/**
 * 受講生コース情報を扱うオブジェクト。
 */
@Schema(description = "受講生コース情報")
@Getter
@Setter
// @ToString
public class StudentCourse {

  private int id;
  private Long studentId;

  @NotBlank
  private String courseName;

  @NotNull
  private LocalDateTime courseStartAt;

  @NotNull
  private LocalDateTime courseEndAt;
}
