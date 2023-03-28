package com.domain.exercise.service;

import com.domain.exercise.entity.EmployeeEnity;
import com.domain.exercise.model.Employee;
import com.domain.exercise.repository.EventStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EventStoreServiceImpl implements EventStoreService {

    @Autowired
    EventStoreRepository eventStoreRepository;

    @Override
    public Optional<EmployeeEnity> verifyEmployeeEntry(int id) {

       return eventStoreRepository.findById(id);

    }

    @Override
    public Optional<EmployeeEnity> isEmployeeEntered(int id) {

        return eventStoreRepository.findById(id);


    }

    @Override
    public void enterExitTime(EmployeeEnity employee) {

        EmployeeEnity employeeEnity = EmployeeEnity.builder()
                .isPresent(false)
                .exitTime(LocalDateTime.now())
                .id(employee.getId())
                .name(employee.getName())
                .entryTime(employee.getEntryTime())
                .build();

        eventStoreRepository.save(employeeEnity);

    }

    @Override
    public void empEntryAfterFirstTime(EmployeeEnity employee) {

        //Optional<EmployeeEnity> byId = eventStoreRepository.findById(employee.getEmpId());


        EmployeeEnity employeeEnity = EmployeeEnity.builder()
                .id(employee.getId())
                .entryTime(employee.getEntryTime())
                .name(employee.getName())
                .isPresent(true)
                .build();

        eventStoreRepository.save(employeeEnity);
    }

    @Override
    public void empEntry(Employee employee) {

         Optional<EmployeeEnity> byId = eventStoreRepository.findById(employee.getEmpId());


        EmployeeEnity employeeEnity = EmployeeEnity.builder()
                .isPresent(true)
                .name(employee.getEmpName())
                .entryTime(LocalDateTime.now())
                .id(employee.getEmpId())
                .build();

        eventStoreRepository.save(employeeEnity);
    }
}
