package com.domain.exercise.resources;

import com.domain.exercise.entity.EmployeeEnity;
import com.domain.exercise.model.Employee;
import com.domain.exercise.service.EventStoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/eventStore/api")
public class EventStoreEntry {


    @Autowired
    EventStoreService eventStoreService;

    @PostMapping("/in")
    public ResponseEntity entry(@RequestBody Employee employee) {
        Optional<EmployeeEnity> employeeEnity = eventStoreService.verifyEmployeeEntry(employee.getEmpId());

        if (employeeEnity.isPresent() && (employeeEnity.get().getEntryTime().getDayOfMonth()==LocalDateTime.now().getDayOfMonth())){
            if (!employeeEnity.get().isPresent()) {
                eventStoreService.empEntryAfterFirstTime(employeeEnity.get());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Already entered need to exit first");
            }

        } else {
            eventStoreService.empEntry(employee);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Entered Successfully");

    }


}
