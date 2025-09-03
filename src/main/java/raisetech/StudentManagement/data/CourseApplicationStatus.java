package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 申込状況を扱うオブジェクト。
 */
@Schema(description = "申込状況")
@Getter
@Setter
// @ToString
public class CourseApplicationStatus {
  /** 主キー */
  private Integer id;

  /** 受講生とコースを紐づけるID */
  private Integer studentCourseId;

  /** 申込状況（仮申込/本申込/受講中/受講終了） */
  @NotBlank(message = "applicationStatusは必須です。")
  private String applicationStatus;

  /** レコード作成日時 */
  private LocalDateTime createdAt;

  /** 最終更新日時 */
  private LocalDateTime lastUpdatedAt;

  /** 最終更新者 */
  private String lastUpdatedBy;

  /** 備考 */
  private String notes;

  /** 論理削除フラグ */
  private Boolean isDeleted;
}
