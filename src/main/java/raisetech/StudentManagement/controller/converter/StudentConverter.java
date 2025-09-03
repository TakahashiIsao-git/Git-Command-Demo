package raisetech.StudentManagement.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.CourseApplicationStatus;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentCourseDetail;
import raisetech.StudentManagement.domain.StudentDetail;

/**
 * 受講生情報とその関連情報（受講生コースと申込状況）を組み合わせて、
 * 受講生詳細に変換するためのコンバーターです。
 * 受講生➡受講生の複数コース➡各コースに申込状況（１：多：１の関係）の構造を持ちます。
 */
@Component
public class StudentConverter {

  /**
   * 受講生リストとそのリストに紐づく受講生コース情報、申込状況情報に基づいて、
   * 受講生詳細情報(StudentDetail)を構築して返します。
   *
   * 受講生（Student）に対して複数の受講生コース（StudentCourse）を紐づけ、
   * 各コースに申込状況（CourseApplicationStatus）をマッピングします。
   *
   * @param studentList 受講生一覧のリスト
   * @param studentCourseList 受講生コース情報のリスト
   * @param applicationStatusList 各受講生コースに対応する申込状況一覧のリスト
   *
   * @return 受講生詳細情報のリスト(受講生とコース詳細を含む)
   */
  public List<StudentDetail> convertStudentDetails(
      List<Student> studentList,
      List<StudentCourse> studentCourseList,
      List<CourseApplicationStatus> applicationStatusList) {
    // TODO: コードが複雑なので、シンプルでより良いコードに修正実装する
    List<StudentDetail> studentDetails = new ArrayList<>();
    // 受講生ごとにループする
    studentList.forEach(student -> {
      // 受講生情報をセットするための箱(studentDetail)を準備する
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);

      // 受講生に紐づく受講生コースを絞り込む
      List<StudentCourseDetail> convertStudentCourseDetailList = studentCourseList.stream()
          .filter(studentCourse -> student.getId().equals(studentCourse.getStudentId()))
          // 受講生コースごとに申込状況をセットする
          // studentCourseを１つずつ取得してstudentCourseDetailの中間の箱に入れ、applicationStatusListの中からコースIDに一致する申込状況を探す
          .map(studentCourse ->{
            StudentCourseDetail studentCourseDetail = new StudentCourseDetail();
            studentCourseDetail.setStudentCourse(studentCourse);

            CourseApplicationStatus matchedApplicationStatus = applicationStatusList.stream()
                .filter(courseApplicationStatus -> courseApplicationStatus.getStudentCourseId().equals(studentCourse.getId()))
                .findFirst() // 最初に見つかった申込状況をセットする
                .orElse(null); // 見つからなければnullとする

            studentCourseDetail.setCourseApplicationStatus(matchedApplicationStatus);
            return studentCourseDetail;
          }).collect(Collectors.toList());

      //  １人の受講生に対して、受講生コースと申込状況がすべて入った状態になる
      studentDetail.setStudentCourseDetailList(convertStudentCourseDetailList);
      studentDetails.add(studentDetail);
    });
    return studentDetails; // 受講生全員をループして完了する
  }
}
