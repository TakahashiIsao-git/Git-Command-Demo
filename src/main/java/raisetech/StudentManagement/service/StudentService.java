package raisetech.StudentManagement.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.CourseApplicationStatus;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentCourseDetail;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

/**
 * 受講生情報を取り扱うサービスです。
 * 受講生の検索、登録や更新処理を行ないます。
 */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter converter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  /**
   * 受講生詳細の一覧検索です。
   * 全件検索を行なうので、条件指定は行わないものになります。
   *
   * @return 受講生一覧(全件)
   */
  public List<StudentDetail> searchStudentList() {
    List<Student> studentList = repository.search();
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    List<CourseApplicationStatus> applicationStatusList = repository.searchCourseApplicationStatusList();
    return converter.convertStudentDetails(studentList, studentCourseList, applicationStatusList);
  }

  /**
   * 名前による受講生の完全一致検索です。
   *
   * @param name 受講生の名前
   * @return 該当する受講生のリスト
   */
  public List<Student> searchStudentByName(String name) {
    return repository.searchStudentByName(name);
  }

  /**
   * Eメールによる受講生の完全一致検索です。
   *
   * @param email 受講生のEメール
   * @return 該当する受講生のリスト
   */
  public List<Student> searchStudentByEmail(String email) {
    return repository.searchStudentByEmail(email);
  }

  /**
   * エリアによる受講生の完全一致検索です。
   *
   * @param area 受講生のエリア
   * @return 該当する受講生のリスト
   */
  public List<Student> searchStudentByArea(String area) {
    return repository.searchStudentByArea(area);
  }

  /**
   * 年齢による受講生の完全一致検索です。
   *
   * @param age 受講生の年齢
   * @return 該当する受講生のリスト
   */
  public List<Student> searchStudentByAge(int age) {
    return repository.searchStudentByAge(age);
  }

  /**
   * 性別による受講生の完全一致検索です。
   *
   * @param sex 受講生の性別
   * @return 該当する受講生のリスト
   */
  public List<Student> searchStudentBySex(String sex) {
    return repository.searchStudentBySex(sex);
  }

  /**
   * 受講生詳細の検索です。
   * IDに紐づく任意の受講生の情報を取得した後、
   * 関連するすべての受講生コース情報および各コースに対する申込状況（CourseApplicationStatus）も合わせて取得・設定します。
   *
   * @param id 受講生ID
   * @return　受講生詳細（１人の受講生・受講生コース・申込状況）
   */
  public StudentDetail searchStudent(String id) {
    Student student = repository.searchStudent(Long.valueOf(id));
    List<StudentCourse> studentCourseDetailList = repository.searchStudentCourse(student.getId());

    // 受講生が登録しているすべてのコースについて、対応する申込状況も取得する処理
    // 受講生コースのリストをストリーム化(順番に処理)
    List<CourseApplicationStatus> applicationStatusList = studentCourseDetailList.stream()
        // コースごとに申込状況をDBから取得する
        .map(studentCourse -> repository.searchCourseApplicationStatus(studentCourse.getId()))
        // null(申込状況が登録されていないコース)を除外する処理
        .filter(courseApplicationStatus -> courseApplicationStatus != null)
        .toList();

    return converter.convertStudentDetails(List.of(student), studentCourseDetailList, applicationStatusList).get(0);
  }

  public  List<StudentCourse> searchStudentCourseList() {
    return repository.searchStudentCourseList();
  }

  /**
   * 受講生詳細の登録です。
   * 受講生情報、受講生コース情報、申込状況情報をまとめて登録します。
   * 受講生コース情報にはコース開始日、コース終了日を設定し、申込状況情報には受講生コースIDを紐づけます。
   *
   * @param studentDetail 受講生詳細
   * @return 申込情報を付与した受講生詳細
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();
    student.setIsDeleted(false);
    repository.registerStudent(student); // student.id がセットされる

    // 受講生コース詳細一覧を１件ずつ取得して対応する受講生コースの登録と申込状況（初期値：仮申込）の登録を行なうループ処理
    studentDetail.getStudentCourseDetailList().forEach(studentCourseDetail -> {
      // 受講生コース詳細から実際の受講生コースを取得する
      StudentCourse studentCourse = studentCourseDetail.getStudentCourse();
      initStudentCourse(studentCourse, student); // studentIdをセット
      // student_courseテーブルにINSERT（申込状況の初回登録 studentCourse.id がセットされる）
      repository.registerStudentCourse(studentCourse);

      // 申込状況の初期値「仮申込」を登録する
      CourseApplicationStatus applicationStatus = new CourseApplicationStatus(
      studentCourse.getId(),
      "仮申込",
      LocalDateTime.now(),
      LocalDateTime.now(),
      "system",
      "初回説明会に参加予定",
      false
      );
      // DBのcourse_application_statusテーブルにデータを登録（INSERT）する
      repository.registerCourseApplicationStatus(applicationStatus);
      // Javaオブジェクトに反映
      studentCourseDetail.setCourseApplicationStatus(applicationStatus);
    });
    return studentDetail;
  }

  /**
   * 受講生コース情報を登録する際の初期情報を設定する。
   *
   * @param studentCourse 受講生コース情報
   * @param student 受講生
   */
  void initStudentCourse(StudentCourse studentCourse, Student student) {
    LocalDateTime now = LocalDateTime.now();

    studentCourse.setStudentId(Long.valueOf(String.valueOf(student.getId())));
    studentCourse.setCourseStartAt(now);
    studentCourse.setCourseEndAt(now.plusYears(1));
  }

  /**
   * 受講生詳細の更新です。受講生と受講生コース情報をそれぞれ更新します。
   *
   * @param studentDetail 受講生詳細
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());

    studentDetail.getStudentCourseDetailList().forEach(studentCourseDetail -> {
         StudentCourse studentCourse = studentCourseDetail.getStudentCourse();
          repository.updateStudentCourse(studentCourse);

          CourseApplicationStatus courseApplicationStatus = studentCourseDetail.getCourseApplicationStatus();
          repository.updateCourseApplicationStatus(studentCourse.getId(), courseApplicationStatus);
    });
  }

  /**
   * 受講生情報の論理削除を行ないます。
   *
   * @param id 受講生ID
   */
  @Transactional
  public void deleteStudent(Long id) {
    repository.deleteStudent(id);
  }

  /**
   * 論理削除した受講生情報の復元を行ないます。
   *
   * @param id 受講生ID
   */
  @Transactional
  public void restoreStudent(Long id) {
    repository.restoreStudent(id);
  }

  /**
   * 申込状況詳細の一覧検索です。
   * 全件検索を行なうので、条件指定は行わないものになります。
   *
   * @return 申込状況一覧(全件)
   */
  @Transactional
  public List<CourseApplicationStatus> searchCourseApplicationStatusList() {
    return repository.searchCourseApplicationStatusList();
  }

  /**
   * 申込状況詳細の検索です。
   *
   * @param studentCourseId 受講生コースID
   * @return 受講生コースIDに紐づく申込状況情報
   */
  @Transactional
  public CourseApplicationStatus searchCourseApplicationStatus(Integer studentCourseId) {
    return repository.searchCourseApplicationStatus(studentCourseId);
  }

  /**
   * 申込状況詳細の登録です。
   *
   * @param courseApplicationStatus 申込状況
   */
  @Transactional
  public void registerCourseApplicationStatus(CourseApplicationStatus courseApplicationStatus) {
    repository.registerCourseApplicationStatus(courseApplicationStatus);
  }

  /**
   * 申込状況詳細の更新です。
   *
   * @param courseApplicationStatus 申込状況
   */
  @Transactional
  public void updateCourseApplicationStatus(Integer studentCourseId, CourseApplicationStatus courseApplicationStatus) {
    repository.updateCourseApplicationStatus(studentCourseId, courseApplicationStatus);
  }

  /**
   * 申込状況詳細の論理削除です。
   *
   * @param id 申込状況ID
   */
  @Transactional
  public void deleteCourseApplicationStatus(Integer id) {
    repository.deleteCourseApplicationStatus(id);
  }
}