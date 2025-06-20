package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 受講生を扱うオブジェクト。
 */
@Schema(description = "受講生")
@Getter
@Setter
public class Student {

  @NotBlank
  @Pattern(regexp = "^\\d+$")
  private Long id;

  @NotBlank
  private String name;

  @NotBlank
  private String kanaName;
  private String nickName;

  @Email
  private String email;

  @NotBlank
  private String area;
  private int age;

  @NotBlank
  private String sex;

  private String remark;
  private Boolean isDeleted;
}
