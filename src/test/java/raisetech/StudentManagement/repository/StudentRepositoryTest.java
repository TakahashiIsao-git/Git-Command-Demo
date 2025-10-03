package raisetech.StudentManagement.repository;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.data.CourseApplicationStatus;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

@MybatisTest
@Transactional // 各テストの最後にロールバックするために設定
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の全件検索が実行できること() {
    List<Student> actual = sut.search();

    assertThat(actual.size()).isEqualTo(16);
  }

  @Test
  void 名前で受講生を完全一致検索できること() {
    List<Student> actual = sut.searchStudentByName("山田太郎");

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual.get(0).getEmail()).isEqualTo("taro@example.com");
  }

  @Test
  void 存在しない名前の検索で空のリストが返ってくること() {
    List<Student> actual = sut.searchStudentByName("テスト名前");

    assertThat(actual).isEmpty();
  }

  @Test
  void Eメールで受講生を完全一致検索できること() {
    List<Student> actual = sut.searchStudentByEmail("ichiro@example.com");

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual.get(0).getName()).isEqualTo("鈴木一郎");
  }

  @Test
  void 存在しないEメールの検索で空のリストが返ってくること() {
    List<Student> actual = sut.searchStudentByEmail("notfound@example.com");

    assertThat(actual).isEmpty();
  }

  @Test
  void エリアで受講生を完全一致検索できること() {
    List<Student> actual = sut.searchStudentByArea("北海道");

    assertThat(actual).isNotEmpty();
    assertThat(actual).extracting(Student::getName).contains("田中花子");
  }

  @Test
  void 存在しないエリアの検索で空のリストが返ってくること() {
    List<Student> actual = sut.searchStudentByArea("南極");

    assertThat(actual).isEmpty();
  }

  @Test
  void 年齢で受講生を完全一致検索できること() {
    List<Student> actual = sut.searchStudentByAge(35);

    assertThat(actual).isNotEmpty();
    assertThat(actual).extracting(Student::getName).contains("伊藤遥", "高橋健太");
  }

  @Test
  void 存在しない年齢の検索で空のリストが返ってくること() {
    List<Student> actual = sut.searchStudentByAge(999);

    assertThat(actual).isEmpty();
  }

  @Test
  void 性別で受講生を完全一致検索できること() {
    List<Student> actual = sut.searchStudentBySex("男性");

    assertThat(actual).isNotEmpty();
    assertThat(actual).extracting(Student::getName).contains(
      "山田太郎", "鈴木一郎", "和田真也", "高橋健太");
  }

  @Test
  void 存在しない性別の検索で空のリストが返ってくること() {
    List<Student> actual = sut.searchStudentBySex("不明");

    assertThat(actual).isEmpty();
  }

  @Test
  void 指定した受講生IDに紐づく受講生検索が実行できること() {
    Student actual = sut.searchStudent(1L);

    assertThat(actual.getName()).isEqualTo("山田太郎");
    assertThat(actual.getKanaName()).isEqualTo("ヤマダタロウ");
    assertThat(actual.getNickName()).isEqualTo("タロ");
    assertThat(actual.getEmail()).isEqualTo("taro@example.com");
    assertThat(actual.getArea()).isEqualTo("東京");
    assertThat(actual.getAge()).isEqualTo(25);
    assertThat(actual.getSex()).isEqualTo("男性");
  }

  @Test
  void 存在しない受講生IDを指定した時にnullが返ってくること() {
    Student actual = sut.searchStudent(999L);

    assertThat(actual).isNull();
  }

  @Test
  void 受講生コース情報の全件検索が実行できること() {
    List<StudentCourse> actual = sut.searchStudentCourseList();

    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 指定した受講生IDに紐づく受講生コースの検索が実行できること() {
    List<StudentCourse> actual = sut.searchStudentCourse(3L);

    assertThat(actual.size()).isEqualTo(3);
    assertThat(actual.get(0).getCourseName()).isEqualTo("Javaコース");
    assertThat(actual.get(1).getCourseName()).isEqualTo("AI・機械学習コース");
    assertThat(actual.get(2).getCourseName()).isEqualTo("ネットワークコース");
  }

  @Test
  void 存在しない受講生IDに紐づく受講生コースの検索で空のリストが返ってくること() {
    List<StudentCourse> actual = sut.searchStudentCourse(999L);

    assertThat(actual).isEmpty();
  }

  @Test
  void 申込状況情報の全件検索が実行できること() {
    List<CourseApplicationStatus> actual = sut.searchCourseApplicationStatusList();

    assertThat(actual.size()).isEqualTo(10);
  }

  @Test // IDに紐づく申込状況情報が検索でき、かつ一部の値が正しいか確認する
  void 指定した受講生コースIDに紐づく申込状況情報の検索が実行できること() {
    CourseApplicationStatus actual101 = sut.searchCourseApplicationStatus(101);
    CourseApplicationStatus actual103 = sut.searchCourseApplicationStatus(103);

    assertThat(actual101).isNotNull();
    assertThat(actual101.getApplicationStatus()).isEqualTo("仮申込");

    assertThat(actual103).isNotNull();
    assertThat(actual103.getLastUpdatedBy()).isEqualTo("system");
  }

  @Test
  void 存在しない受講生コースIDに紐づく申込状況情報を検索したときにnullが返ってくること() {
    CourseApplicationStatus actual = sut.searchCourseApplicationStatus(999);

    assertThat(actual).isNull();
  }

  @Test // すべてのフィールドが取得でき、データの整合性が保たれているか確認する
  void 申込状況情報の各フィールドが適切に取得できること() {
    CourseApplicationStatus actual = sut.searchCourseApplicationStatus(110);

    assertThat(actual.getApplicationStatus()).isEqualTo("仮申込");
    assertThat(actual.getLastUpdatedBy()).isEqualTo("staff02");
    assertThat(actual.getNotes()).isEqualTo("オンライン説明会参加済み");
  }

  @Test
  void 受講生の登録が実行できること() {
    Student student = new Student(
    "テスト太郎",
    "テストタロウ",
    "テスト",
    "test@example.com",
    "愛知",
    25,
    "男性",
    "",
    false
    );
    sut.registerStudent(student);

    List<Student> actual = sut.search();

    assertThat(actual.size()).isEqualTo(17);
  }

  // NotNull制約があるカラムをnullにして登録しようとすると例外が発生すること
  @Test
  void 受講生情報の必須項目がnullの受講生を登録する時に例外がスローされること() {
    Student student = new Student();
    student.setKanaName("タロ");
    // nameをnull(必須項目)
    // emailをnull(必須項目)

    org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
      sut.registerStudent(student);
    });
  }

  @Test
  void 受講生コース情報の登録が実行できること() {
    StudentCourse studentCourse = new StudentCourse(
    1L,
    "セキュリティコース",
    LocalDateTime.of(2025, 6, 1, 9, 0, 0),
    LocalDateTime.of(2025, 10, 1, 17, 0, 0)
    );
    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourse(1L);

    assertThat(actual.size()).isEqualTo(3);
    assertThat(actual).extracting(StudentCourse::getCourseName)
        .contains("セキュリティコース");
  }

  // ④DBでNot NULL制約がある項目をnullした状態で受講生コースのデータ登録をした時にエラーが発生する
  @Test
  void 受講生コース情報の必須項目がnullの受講生コースが登録できず例外がスローされること() {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(1L);
    // courseNameをnull(必須項目)
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

    org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
      sut.registerStudentCourse(studentCourse);
    });
  }

  @Test // 正常にデータベースへ登録できるか
  void 申込状況情報の登録が実行できること() {
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus(
    101,
    "仮申込",
        LocalDateTime.now(),
        LocalDateTime.now(),
    "admin",
    "初回問い合わせあり",
    false
    );

    sut.registerCourseApplicationStatus(courseApplicationStatus);

    CourseApplicationStatus actual = sut.searchCourseApplicationStatus(101);
    assertThat(actual.getApplicationStatus()).isEqualTo("仮申込");
    assertThat(actual.getLastUpdatedBy()).isEqualTo("admin");
    assertThat(actual.getNotes()).isEqualTo("初回問い合わせあり");
  }

  @Test // 必須項目のフィールドが不足している場合に例外が発生するか
  void 申込状況情報の必須項目がnullの受講生コースが登録できず例外がスローされること() {
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    courseApplicationStatus.setStudentCourseId(102);
    // applicationStatusをnull(必須項目)
    courseApplicationStatus.setLastUpdatedBy("admin");
    courseApplicationStatus.setNotes("出席率95%");

    org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
      sut.registerCourseApplicationStatus(courseApplicationStatus);
    });
  }

  @Test
  void 受講生の更新が実行できること() {
    Student student = sut.searchStudent(4L);
    student.setArea("長崎");
    student.setAge(24);

    sut.updateStudent(student);

    Student actual = sut.searchStudent(4L);

    assertThat(actual.getArea()).isEqualTo("長崎");
    assertThat(actual.getAge()).isEqualTo(24);
  }

  @Test
  void 存在しない受講生の更新時には例外がスローされないこと() {
    Student student = new Student();
    student.setId(999L); // 存在しないID
    student.setName("テスト太郎");
    student.setKanaName("タロウ");
    student.setEmail("taro@example.com");

    sut.updateStudent(student);
  }

  @Test
  void 受講生コース情報のコース名の更新が実行できること() {
    List<StudentCourse> studentCourses = sut.searchStudentCourse(5L);
    StudentCourse studentCourse = studentCourses.get(0);

    studentCourse.setCourseName("改訂版ネットワークコース");
    sut.updateStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourse(5L);

    assertThat(actual.get(0).getCourseName()).isEqualTo("改訂版ネットワークコース");
  }

  @Test
  void 存在しない受講生コース情報の更新時には例外がスローされないこと() {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(999);
    studentCourse.setStudentId(1L);
    studentCourse.setCourseName("C++プログラムコース");
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

    sut.updateStudentCourse(studentCourse);
  }

  @Test
  void 申込状況情報の更新が実行できること() {
    CourseApplicationStatus courseApplicationStatus = sut.searchCourseApplicationStatus(101);
    courseApplicationStatus.setApplicationStatus("本申込");
    courseApplicationStatus.setLastUpdatedBy("staff01");
    courseApplicationStatus.setNotes("説明会終了");

    sut.updateCourseApplicationStatus(101, courseApplicationStatus);

    CourseApplicationStatus actual = sut.searchCourseApplicationStatus(101);

    assertThat(actual.getApplicationStatus()).isEqualTo("本申込");
    assertThat(actual.getLastUpdatedBy()).isEqualTo("staff01");
    assertThat(actual.getNotes()).isEqualTo("説明会終了");
  }

  @Test
  void 存在しない申込状況情報のIDの更新時には例外がスローされないこと() {
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    courseApplicationStatus.setApplicationStatus("キャンセル");
    courseApplicationStatus.setCreatedAt(LocalDateTime.now());
    courseApplicationStatus.setLastUpdatedAt(LocalDateTime.now());
    courseApplicationStatus.setLastUpdatedBy("admin");
    courseApplicationStatus.setNotes("連絡なしで不参加");

    sut.updateCourseApplicationStatus(999, courseApplicationStatus);

    CourseApplicationStatus actual = sut.searchCourseApplicationStatus(999);
    assertThat(actual).isNull();
  }

  @Test
  void 受講生の論理削除が実行できること() {
    // 対象の受講生を確認
    Student student = new Student(
    "テスト太郎",
    "テストタロウ",
    "テスト",
    "test@example.com",
    "愛知",
    25,
    "男性",
    "テスト用データ",
    false
    );
    sut.registerStudent(student);

    Student expected = sut.searchStudent(student.getId());
    assertThat(expected.getIsDeleted()).isFalse();

    // 論理削除の実行
    sut.deleteStudent(student.getId());

    // 論理削除した受講生を取得
    Student actual = sut.searchStudent(student.getId());

    //論理削除の検証
    assertThat(actual.getIsDeleted()).isTrue(); // 削除後はtrue
  }

  @Test
  @BeforeEach
  void 論理削除された受講生の復元が実行できること() {
    Student student = new Student(
    "江藤由紀子",
    "エトウユキコ",
    "ユキコ",
    "yukiko@example.com",
    "奈良",
    46,
    "女性",
    "",
    true // 最初から論理削除状態として設定
    );
    sut.registerStudent(student);

    // 登録したIDを取得
    Long id = student.getId();
    sut.restoreStudent(id);

    Student actual = sut.searchStudent(id);
    assertThat(actual).isNotNull();
    assertThat(actual.getIsDeleted()).isFalse();
  }

  @Test
  void 存在しない受講生IDの復元処理の時に例外がスローされないこと() {
    sut.restoreStudent(999L);
  }

  @Test
  void 指定した申込状況の論理削除が実行され削除フラグ_isDeletedがtrueになること() {
    CourseApplicationStatus beforeCourseApplicationStatus = sut.searchCourseApplicationStatus(101);
    // 削除前にレコードが存在していることを確認
    assertThat(beforeCourseApplicationStatus).isNotNull();
    assertThat(beforeCourseApplicationStatus.getIsDeleted()).isFalse();

    sut.deleteCourseApplicationStatus(1);

    // 削除後もレコードが論理削除されている状態で残っているか確認(物理削除されていない)
    CourseApplicationStatus afterCourseApplicationStatus = sut.searchCourseApplicationStatus(101);
    assertThat(afterCourseApplicationStatus).isNotNull();
    assertThat(afterCourseApplicationStatus.getIsDeleted()).isTrue();
  }

  @Test
  void 存在しない申込状況IDを削除しても例外がスローされないこと() {
    // nullや存在しないIDで削除してもエラーにならないか確認
    sut.deleteCourseApplicationStatus(999);
  }
}
