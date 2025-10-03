package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import raisetech.StudentManagement.data.CourseApplicationStatus;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

/**
 * 受講生テーブルと受講生コース情報テーブルと紐づくRepositoryです。
 */
@Mapper
public interface StudentRepository {

  /**
   * 受講生の全件検索を行ないます。
   * 論理削除されたレコードは対象外です。
   *
   * @return 受講生の一覧情報(全件、論理削除済みは除外)
   */
  List<Student> search();

  /**
   * 受講生の検索を行ないます。
   *
   * @param id 受講生ID
   * @return 受講生情報
   */
  Student searchStudent(Long id);

  /**
   * 名前による完全一致検索を行ないます。
   *
   * @param name 検索対象の受講生の名前
   * @return 該当する受講生のリスト（該当なしの場合は空リスト）
   */
  List<Student> searchStudentByName(String name);

  /**
   * Ｅメールによる完全一致検索を行ないます。
   *
   * @param email 検索対象の受講生のEメール
   * @return 該当する受講生のリスト（該当なしの場合は空リスト）
   */
  List<Student> searchStudentByEmail(String email);

  /**
   * エリアによる完全一致検索を行ないます。
   *
   * @param area 検索対象の受講生のエリア
   * @return 該当する受講生のリスト（該当なしの場合は空リスト）
   */
  List<Student> searchStudentByArea(String area);

  /**
   * 年齢による完全一致検索を行ないます。
   *
   * @param age 検索対象の受講生の年齢
   * @return 該当する受講生のリスト（該当なしの場合は空リスト）
   */
  List<Student> searchStudentByAge(int age);

  /**
   * 性別による完全一致検索を行ないます。
   *
   * @param sex 検索対象の受講生の性別
   * @return 該当する受講生のリスト（該当なしの場合は空リスト）
   */
  List<Student> searchStudentBySex(String sex);

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
   *  申込状況情報の全件検索を行ないます。
   *
   * @return 申込状況のリスト情報（全件）
   */
  List<CourseApplicationStatus> searchCourseApplicationStatusList();

  /**
   *  受講生コースIDに紐づく申込状況情報を検索します。
   *
   * @param studentCourseId 受講生コースID
   * @return 受講生コースIDに紐づく申込状況情報（該当しなければnullも可）
   */
  CourseApplicationStatus searchCourseApplicationStatus(@Param("studentCourseId") Integer studentCourseId);

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
   * 受講生コースの申込状況情報を新規登録します。IDに関しては自動採番を行なう。
   *
   * @param courseApplicationStatus 申込状況情報
   */
  void registerCourseApplicationStatus(CourseApplicationStatus courseApplicationStatus);

  /**
   * 受講生を更新します。
   *
   * @param student 受講生
   */
  void updateStudent(Student student);

  /**
   * 受講生コース情報のコース名を更新します。
   *
   * ※テストでは更新後にsearchを使ってデータが変更されていることを確認してください。
   * @param studentCourse 受講生コース情報
   */
  // 受講生コース情報の更新処理 Testクラス作成の場合はデータの内容が更新されたことも確認する！searchを使用
  void updateStudentCourse(StudentCourse studentCourse);

  /**
   * 任意の受講生コースIDに紐づく申込状況情報を更新します。
   * 更新対象は申込状況、更新者、メモです。
   *
   * @param studentCourseId 対象の受講生コースID
   * @param courseApplicationStatus 更新内容(申込状況、更新者、メモを含む）
   */
  void updateCourseApplicationStatus(
      // 複数の引数を設定するために@Paramをセット
      @Param("studentCourseId") Integer studentCourseId,
      @Param("courseApplicationStatus") CourseApplicationStatus courseApplicationStatus);

  /**
   * 論理削除された受講生情報を削除フラグ付きで更新します。
   * 物理削除ではありません。
   *
   * @param id 受講生ID
   */
  void deleteStudent(Long id);

  /**
   * 論理削除でキャンセルした受講生情報を復元します。
   *
   * @param id 受講生ID
   */
  void restoreStudent(Long id);

  /**
   * 論理削除された申込状況情報を削除フラグ付きで更新します。
   * 物理削除ではありません。
   *
   * @param id 論理削除対象の申込状況ID
   */
  void deleteCourseApplicationStatus(Integer id);
}
