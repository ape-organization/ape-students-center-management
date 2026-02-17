package com.example.student.service;

import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentResponse;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
}

