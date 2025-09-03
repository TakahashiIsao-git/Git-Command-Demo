package raisetech.StudentManagement.data;

// import lombok.ToString;
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
// @ToString
public class Student {

  private Long id;

  @NotBlank
  private String name;

  @NotBlank
  private String kanaName;

  @NotBlank
  private String nickName;

  @NotBlank(message = "メールは必須です。")
  @Email(message = "正しいメールアドレスを入力してください。")
  private String email;

  @NotBlank
  private String area;

  private int age;

  @NotBlank
  private String sex;

  private String remark;
  private Boolean isDeleted;
}
