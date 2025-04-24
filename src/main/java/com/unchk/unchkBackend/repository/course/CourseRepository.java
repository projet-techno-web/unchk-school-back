package com.unchk.unchkBackend.repository.course;

import com.unchk.unchkBackend.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
