package com.domain.exercise.resources;

import com.domain.exercise.entity.EmployeeEntity;
import com.domain.exercise.model.Employee;
import com.domain.exercise.service.EventStoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/eventStore/api")
public class EventStoreEntry {

    @Autowired
    EventStoreService eventStoreService;

    @PostMapping("/in")
    public ResponseEntity entry(@RequestBody Employee employee) {
        Optional<EmployeeEntity> employeeEnity = eventStoreService.verifyEmployeeEntry(employee.getEmpId());
        // logic to check either employee entered in store for today
        if (employeeEnity.isPresent() && (employeeEnity.get().getEntryTime().getDayOfMonth()==LocalDateTime.now().getDayOfMonth())){
            if (employeeEnity.get().isAlreadyEntered()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Already entered need to exit first");
            } else {
                eventStoreService.empEntryAfterFirstTime(employeeEnity.get());
            }

        } else {
            // employee enter in store first time in a day
            eventStoreService.empEntry(employee);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Entered Successfully");

    }

    @GetMapping("/calculate-attendance/{empId}")
    public ResponseEntity calculateAttendance(@PathVariable int empId){
         double attendanceInMinutes = eventStoreService.calculateAttendance(empId);
         return ResponseEntity.status(HttpStatus.OK).body(attendanceInMinutes);
    }
}
