package raisetech.StudentManagement.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;

/**
 * 受講生詳細を受講生と受講生コース情報、もしくはその逆の変換を行なうコンバーターです。
 */
@Component
public class StudentConverter {

  /**
   * 受講生に紐づく受講生コース情報をマッピングする。
   * 受講生コース情報は受講生に対して複数存在するため、ループを回して受講生詳細情報を組み立てる。
   *
   * @param students 受講生一覧
   * @param studentsCourses 受講生コース情報のリスト
   * @return 受講生詳細情報のリスト
   */
  public List<StudentDetail> convertStudentDetails(List<Student> students,
      List<StudentsCourses> studentsCourses) {
    // TODO: コードが複雑なので、シンプルでより良いコードに修正実装する
    List<StudentDetail> studentDetails = new ArrayList<>();
    students.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);

      List<StudentsCourses> convertStudentsCourses = studentsCourses.stream()
          .filter(studentsCourses1 -> student.getId().equals(studentsCourses1.getStudentId()))
          .collect(Collectors.toList());

      studentDetail.setStudentsCourses(convertStudentsCourses);
      studentDetails.add(studentDetail);
    });
    return studentDetails;
  }
}
