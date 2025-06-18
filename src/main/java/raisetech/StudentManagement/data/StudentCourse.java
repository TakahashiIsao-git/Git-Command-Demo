package raisetech.StudentManagement.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 受講生コース情報を扱うオブジェクト。
 */
@Getter
@Setter
public class StudentCourse {

  private String id;
  private Long studentId;

  @NotBlank(message = "コース名は必須です。")
  private String courseName;

  @NotNull
  private LocalDateTime courseStartAt;

  @NotNull
  private LocalDateTime courseEndAt;
}
