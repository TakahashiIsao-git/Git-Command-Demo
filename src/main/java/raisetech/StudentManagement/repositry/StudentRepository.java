package raisetech.StudentManagement.repositry;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;

@Mapper
public interface StudentRepository {

  // 受講生情報を検索する(論理削除のレコードを一覧画面に表示させない)
  @Select("SELECT * FROM students")
  List<Student> search();

  // idに基づいた単一の受講生情報を検索する
  @Select("SELECT * FROM students WHERE id = #{id}")
  Student searchStudent(String id);

  // 単一の受講生コースを検索する
  @Select("SELECT * FROM students_courses")
  List<StudentsCourses> searchStudentsCoursesList();

  //　特定の受講生idに基づいた受講生コース情報を検索する
  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentsCourses> searchStudentsCourses(Long studentId);

  //　受講生情報の登録処理
  @Insert("INSERT INTO students (name, kana_name, nickName, email, area, age, sex, remark, isDeleted) " +
      "VALUES (#{name}, #{kanaName}, #{nickName}, #{email}, #{area}, #{age}, #{sex}, #{remark}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id") // DBがidを取得できるようにする
  void registerStudent(Student student);

  // 受講生コース情報の登録処理
  @Insert("INSERT INTO students_courses (student_id, course_name, course_start_at, course_end_at) " +
      "VALUES (#{studentId}, #{courseName}, #{courseStartAt}, #{courseEndAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id") // DBがidを取得できるようにする
  void registerStudentsCourses(StudentsCourses studentsCourses);

  // 受講生情報の更新処理
  @Update("UPDATE students SET name = #{name}, kana_name = #{kanaName}, nickName = #{nickName}, "
      + "email = #{email}, area = #{area}, age = #{age}, sex = #{sex}, remark = #{remark}, isDeleted = #{isDeleted} "
      + "WHERE id = #{id}")
  void updateStudent(Student student);

  // 受講生コース情報の更新処理　WHERE後のidをstudentIdに変更してはダメ（idの検索を繰り返したい）➡Repositoryを変えずに別の方法を考える！
  @Update("UPDATE students_courses SET course_name = #{courseName} WHERE id = #{id}")
  void updateStudentsCourses(StudentsCourses studentsCourses);

  // 論理削除でキャンセルした受講生情報を復元する
  @Update("UPDATE students SET isDeleted = false WHERE id = #{id}")
  void restoreStudent(Long id);
}
