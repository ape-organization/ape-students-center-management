package com.example.student;

import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void saveAndFindByEmail() {
        Student s = new Student("Alice", "Smith", "alice@example.com");
        Student saved = studentRepository.save(s);

        assertThat(saved.getId()).isNotNull();

        var found = studentRepository.findByEmail("alice@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Alice");
    }
}
