CREATE TABLE IF NOT EXISTS students
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	kana_name VARCHAR(50) NOT NULL,
	nick_name VARCHAR(50),
	email VARCHAR(50) NOT NULL,
	area VARCHAR(50),
	age INT,
	sex VARCHAR(10),
	remark TEXT,
	isDeleted boolean
);

CREATE TABLE IF NOT EXISTS students_courses
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	student_id INT NOT NULL,
	course_name VARCHAR(50) NOT NULL,
	course_start_at TIMESTAMP,
	course_end_at TIMESTAMP,
	FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS course_application_status (
  id INT NOT NULL AUTO_INCREMENT,
  student_course_id INT NOT NULL,
  application_status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(50) DEFAULT NULL,
  notes text,
  isDeleted BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (id),
  FOREIGN KEY (student_course_id) REFERENCES students_courses(id)
);
