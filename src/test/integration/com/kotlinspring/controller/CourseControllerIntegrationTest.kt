package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.util.courseEntityList
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CourseControllerIntegrationTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun setup() {
        courseRepository.deleteAll()
        val courses = courseEntityList()
        courseRepository.saveAll(courses)
    }

    @Test
    fun addCourse() {
        val courseDto = CourseDTO(null,"Build Restful APIs using SpringBoot and Kotlin","Arcadio")

        val savedCourseDTO = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDto)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue{
            savedCourseDTO!!.id != null
        }
    }

    @Test
    fun retrieveAllCourses(){
        val courseDTOs = webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody
        println("CourseDtos: $courseDTOs")
        assertEquals(3, courseDTOs!!.size)
    }

    @Test
    fun updateCourse(){
        //existing course
        val course = Course(null, "Build restful API with springboot and Kotlin", "Development")
        courseRepository.save(course)

        //courseId
        //Updated courseDTO
        val updatedCourseDTO  = CourseDTO(null, "Arcadio Ramos", "Development")
        val updatedCourse = webTestClient
            .put()
            .uri("/v1/courses/{course_id}", course.id)
            .bodyValue(updatedCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals("Arcadio Ramos", updatedCourse!!.name)

    }

    @Test
    fun deleteCourse(){
        //existing course
        val course = Course(null, "Build restful API with springboot and Kotlin", "Development")
        courseRepository.save(course)

        val deletedCourse = webTestClient
            .delete()
            .uri("/v1/courses/{course_id}", course.id)
            .exchange()
            .expectStatus().isNoContent

    }
}