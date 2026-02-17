package com.example.student.service.impl;

import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentResponse;
import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import com.example.student.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentResponse createStudent(StudentRequest request) {
        Student student = new Student(request.getFirstName(), request.getLastName(), request.getEmail());
        student = studentRepository.save(student);
        return new StudentResponse(student.getId(), student.getFirstName(), student.getLastName(), student.getEmail());
    }
}

