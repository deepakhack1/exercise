package com.domain.exercise.service;

import com.domain.exercise.entity.EmployeeEntity;
import com.domain.exercise.model.Employee;
import com.domain.exercise.model.KafkaPayload;
import com.domain.exercise.repository.EventStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;



@Service
public class EventStoreServiceImpl implements EventStoreService {

    @Autowired
    EventStoreRepository eventStoreRepository;

    @Autowired
    KafkaProducer kafkaProducer;

    @Override
    public Optional<EmployeeEntity> verifyEmployeeEntry(int id) {

        return eventStoreRepository.findById(id);

    }

    @Override
    public Optional<EmployeeEntity> isEmployeeEntered(int id) {

        return eventStoreRepository.findById(id);


    }

    @Override
    public void enterExitTime(EmployeeEntity employee) {

        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .isPresent(false)
                .exitTime(LocalDateTime.now())
                .id(employee.getId())
                .name(employee.getName())
                .entryTime(employee.getEntryTime())
                .build();

        eventStoreRepository.save(employeeEntity);

    }

    @Override
    public void empEntryAfterFirstTime(EmployeeEntity employee) {

        //Optional<EmployeeEnity> byId = eventStoreRepository.findById(employee.getEmpId());


        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .id(employee.getId())
                .entryTime(employee.getEntryTime())
                .name(employee.getName())
                .isPresent(true)
                .build();

        eventStoreRepository.save(employeeEntity);
    }

    @Override
    public double calculateAttendance(int empId) {

        EmployeeEntity employeeEntity = eventStoreRepository.findById(empId).get();
        LocalTime entryTime = employeeEntity.getEntryTime().toLocalTime();
        LocalTime exitTime = employeeEntity.getExitTime().toLocalTime();

        double minutes = Duration.between(entryTime, exitTime).toMinutes();

        double hour = minutes/60.0D;

        KafkaPayload kafkaPayload = KafkaPayload.builder()
                        .attendance(hour)
                                .empId(employeeEntity.getId())
                                        .name(employeeEntity.getName()).build();

        kafkaProducer.sendMessage(kafkaPayload );

        return hour;
    }

    @Override
    public void empEntry(Employee employee) {

        Optional<EmployeeEntity> byId = eventStoreRepository.findById(employee.getEmpId());


        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .isPresent(true)
                .name(employee.getEmpName())
                .entryTime(LocalDateTime.now())
                .id(employee.getEmpId())
                .build();

        eventStoreRepository.save(employeeEntity);
    }
}
