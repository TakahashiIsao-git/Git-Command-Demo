package raisetech.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

  private Long id;
  private String name;
  private String kanaName;
  private String nickName;
  private String email;
  private String area;
  private int age;
  private String sex;
  private String remark;
  private Boolean isDeleted;
}
