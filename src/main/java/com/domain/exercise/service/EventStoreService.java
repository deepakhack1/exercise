package com.domain.exercise.service;

import com.domain.exercise.entity.EmployeeEnity;
import com.domain.exercise.model.Employee;

import java.util.Optional;

public interface EventStoreService {

    Optional<EmployeeEnity> verifyEmployeeEntry(int id);

    void empEntry(Employee employee);

   Optional<EmployeeEnity> isEmployeeEntered(int id);

    void enterExitTime(EmployeeEnity employee);

    void empEntryAfterFirstTime(EmployeeEnity employee);

}
