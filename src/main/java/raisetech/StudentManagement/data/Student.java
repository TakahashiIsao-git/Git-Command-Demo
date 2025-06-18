package raisetech.StudentManagement.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 受講生を扱うオブジェクト。
 */
@Getter
@Setter
public class Student {

  private Long id;

  @NotBlank(message = "名前は必須です。")
  private String name;

  @NotBlank(message = "カナ名は必須です。")
  private String kanaName;
  private String nickName;

  @Email(message = "正しいメールアドレスを入力してください。")
  private String email;

  @NotBlank(message = "地域名は必須です。")
  private String area;
  private int age;
  private String sex;
  private String remark;
  private Boolean isDeleted;
}
