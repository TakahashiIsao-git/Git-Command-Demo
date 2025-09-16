package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 受講生を扱うオブジェクト。
 */
@Schema(description = "受講生")
@Getter
@Setter
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

  /** 引数付きコンストラクタ 本番用(idなし) */
  public Student(
      String name,
      String kanaName,
      String nickName,
      String email,
      String area,
      int age,
      String sex,
      String remark,
      Boolean isDeleted
  ) {
    this.name = name;
    this.kanaName = kanaName;
    this.nickName = nickName;
    this.email = email;
    this.area = area;
    this.age = age;
    this.sex = sex;
    this.remark = remark;
    this.isDeleted = isDeleted;
  }

  /** 引数付きコンストラクタ テスト専用(idあり) */
  public Student(Long id, String name, String kanaName, String nickName, String email,
      String area, int age, String sex, String remark, Boolean isDeleted) {
    this(name, kanaName, nickName, email, area, age, sex, remark, isDeleted);
    this.id = id;
  }

  /** JPA用にデフォルトコンストラクタ */
  public Student() {}

}
