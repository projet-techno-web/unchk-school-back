package com.unchk.unchkBackend.controller.course;

import com.unchk.unchkBackend.model.course.Course;
import com.unchk.unchkBackend.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // Créer un cours avec une image
    @PostMapping
    public ResponseEntity<Course> createCourse(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        Course course = courseService.createCourse(title, description, imageFile);
        return ResponseEntity.ok(course);
    }

    // Récupérer tous les cours
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // Récupérer un cours par ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Mettre à jour un cours
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {
        return courseService.updateCourse(id, title, description, imageFile)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Supprimer un cours
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        boolean deleted = courseService.deleteCourse(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
