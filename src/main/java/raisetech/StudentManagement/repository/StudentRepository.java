package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

/**
 * 受講生テーブルと受講生コース情報テーブルと紐づくRepositoryです。
 */
@Mapper
public interface StudentRepository {

  /**
   * 受講生の全件検索を行ないます。
   *
   * @return 受講生一覧(全件)
   */
  /** 論理削除のレコードを一覧画面に表示させない */
  // @Select("SELECT * FROM students WHERE isDeleted = false")
  List<Student> search();

  /**
   * 受講生の検索を行ないます。
   *
   * @param id 受講生ID
   * @return 受講生
   */
  Student searchStudent(Long id);

  /**
   * 受講生のコース情報の全件検索を行ないます。
   *
   * @return 受講生のコース情報(全件)
   */
  List<StudentCourse> searchStudentCourseList();

  /**
   * 受講生IDに紐づく受講生コース情報を検索します。
   *
   * @param studentId 受講生ID
   * @return 受講生IDに紐づく受講生コース情報
   */
  List<StudentCourse> searchStudentCourse(Long studentId);

  /**
   * 受講生を新規登録します。IDに関しては自動採番を行なう。
   *
   * @param student 受講生
   */
  void registerStudent(Student student);

  /**
   * 受講生コース情報を新規登録します。IDに関しては自動採番を行なう。
   *
   * @param studentCourse 受講生コース情報
   */
  void registerStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生を更新します。
   *
   * @param student 受講生
   */
  void updateStudent(Student student);

  /**
   * 受講生コース情報のコース名を更新します。
   *
   * @param studentCourse 受講生コース情報
   */
  // 受講生コース情報の更新処理
  void updateStudentCourse(StudentCourse studentCourse);

  /**
   * 論理削除でキャンセルした受講生情報を復元します。
   *
   * @param id 受講生ID
   */
  void restoreStudent(Long id);
}
