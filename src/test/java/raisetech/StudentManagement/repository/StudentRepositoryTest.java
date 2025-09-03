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

    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void 指定した受講生IDに紐づく受講生検索が実行できること() {
    Student actual = sut.searchStudent(1L);

    /* データの内容の確認
    System.out.println(actual); */
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

    /* データの内容の確認
    System.out.println(actual); */
    assertThat(actual.size()).isEqualTo(9);
  }

  @Test
  void 指定した受講生IDに紐づく受講生コースの検索が実行できること() {
    List<StudentCourse> actual = sut.searchStudentCourse(3L);

    /* データの内容の確認
    System.out.println(actual); */
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

    assertThat(actual.size()).isEqualTo(9);
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
    Student student = new Student();
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("test@example.com");
    student.setArea("愛知");
    student.setAge(25);
    student.setSex("男性");
    student.setRemark("");
    student.setIsDeleted(false);

    sut.registerStudent(student);

    List<Student> actual = sut.search();

    /* データの内容の確認
    System.out.println(actual); */
    assertThat(actual.size()).isEqualTo(6);
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
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(1L);
    studentCourse.setCourseName("セキュリティコース");
    studentCourse.setCourseStartAt(LocalDateTime.of(2025, 6, 1, 9, 0, 0));
    studentCourse.setCourseEndAt(LocalDateTime.of(2025, 10, 1, 17, 0, 0));

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourse(1L);

    /* データの内容の確認
    System.out.println(actual); */
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
    CourseApplicationStatus courseApplicationStatus = new CourseApplicationStatus();
    courseApplicationStatus.setStudentCourseId(101);
    courseApplicationStatus.setApplicationStatus("仮申込");
    courseApplicationStatus.setLastUpdatedBy("admin");
    courseApplicationStatus.setNotes("初回問い合わせあり");

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

    /* データの更新内容の確認
    System.out.println(actual); */
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

    /* データの更新内容の確認
    System.out.println(actual); */
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
    Student student = new Student();
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setNickName("テスト");
    student.setEmail("test@example.com");
    student.setArea("愛知");
    student.setAge(25);
    student.setSex("男性");
    student.setRemark("テスト用データ");
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
    Student student = new Student();
    student.setName("江藤由紀子");
    student.setKanaName("エトウユキコ");
    student.setNickName("ユキコ");
    student.setEmail("yukiko@example.com");
    student.setArea("奈良");
    student.setAge(46);
    student.setSex("女性");
    student.setRemark("");
    student.setIsDeleted(true); // 最初から論理削除状態として設定
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
