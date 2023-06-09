package com.domain.exercise.resources;

import com.domain.exercise.entity.EmployeeEntity;
import com.domain.exercise.service.EventStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/eventStore/api")
public class EventStoreExit {

    @Autowired
    EventStoreService eventStoreService;

    @GetMapping("/out")
    public ResponseEntity exit(@RequestParam int  empId) {

        Optional<EmployeeEntity> employeeEntered = eventStoreService.isEmployeeEntered(empId);

        if (employeeEntered.isPresent()) {
            if (employeeEntered.get().isAlreadyEntered()) {
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
