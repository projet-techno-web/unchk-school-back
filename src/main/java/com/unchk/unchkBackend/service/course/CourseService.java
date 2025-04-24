package com.unchk.unchkBackend.service.course;

import com.unchk.unchkBackend.model.course.Course;
import com.unchk.unchkBackend.repository.course.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @Value("${upload.directory:c:/temp/uploads}")
    private String uploadDir;

    public Course createCourse(String title, String description, MultipartFile imageFile) throws IOException {
        String imageUrl = saveImage(imageFile);

        Course course = Course.builder()
                .title(title)
                .description(description)
                .imageUrl(imageUrl)
                .build();

        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> updateCourse(Long id, String title, String description, MultipartFile imageFile) throws IOException {
        return courseRepository.findById(id).map(course -> {
            course.setTitle(title);
            course.setDescription(description);

            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imageUrl = saveImage(imageFile);
                    course.setImageUrl(imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Erreur lors de l'upload de l'image", e);
                }
            }

            return courseRepository.save(course);
        });
    }

    public boolean deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path imagePath = Paths.get(uploadDir, filename);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, imageFile.getBytes());
        return "/uploads/" + filename; // Utilisé comme URL dans le frontend
    }
}
