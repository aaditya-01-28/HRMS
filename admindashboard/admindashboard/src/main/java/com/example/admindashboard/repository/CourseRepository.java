package com.example.admindashboard.repository;

import com.example.admindashboard.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    long countByStatus(String status);
    @Query("""
    		SELECT c.category, COUNT(c)
    		FROM Course c
    		GROUP BY c.category
    		""")
	List<Object[]> getCategoryDistribution();
	List<Course> findByCourseType(String courseType);

	List<Course> findByStatus(String status);

	List<Course> findByCourseNameContainingIgnoreCase(String courseName);

	List<Course> findByCourseTypeAndStatus(
	        String courseType,
	        String status);
}