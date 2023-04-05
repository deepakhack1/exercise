package com.domain.exercise.service;

import com.domain.exercise.entity.EmployeeEntity;
import com.domain.exercise.model.Employee;

import java.util.Optional;

public interface EventStoreService {

    Optional<EmployeeEntity> verifyEmployeeEntry(int id);

    void empEntry(Employee employee);

   Optional<EmployeeEntity> isEmployeeEntered(int id);

    void enterExitTime(EmployeeEntity employee);

    void empEntryAfterFirstTime(EmployeeEntity employee);

    double calculateAttendance(int empId);

}
