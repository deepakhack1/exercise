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

import java.util.Optional;

@RestController
@RequestMapping("/eventStore/api")
public class EventStoreExit {

    @Autowired
    EventStoreService eventStoreService;

    @PostMapping("/out")
    public ResponseEntity exit(@RequestBody Employee employee) {

        Optional<EmployeeEnity> employeeEntered = eventStoreService.isEmployeeEntered(employee.getEmpId());

        if (employeeEntered.isPresent()) {
            if (employeeEntered.get().isPresent()) {
                eventStoreService.enterExitTime(employeeEntered.get());
            }else {
               return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Employee is not entered");
            }
        } else {
         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Employee is not entered");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Exited successfully");

    }

}
