package com.example.student.service;

import com.example.student.generated.model.StudentRequest;
import com.example.student.generated.model.StudentResponse;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
}
