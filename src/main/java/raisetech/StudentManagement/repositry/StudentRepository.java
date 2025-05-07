package raisetech.StudentManagement.repositry;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;

@Mapper
public interface StudentRepository {

  // 全件検索用
  @Select("SELECT * FROM students")
  List<Student> search();

  // 年齢を30代に限定して絞り込み検索する用
  @Select("SELECT * FROM students WHERE age >= 30 && age < 40")
  List<Student> searchStudentInThirty();

  @Select("SELECT * FROM students_courses")
  List<StudentsCourses> searchStudentsCourses();
}
