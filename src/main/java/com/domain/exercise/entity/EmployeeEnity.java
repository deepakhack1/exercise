package com.domain.exercise.entity;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "Employee")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EmployeeEnity {
    @Id
    @NonNull
    private int id;
    private String name;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private boolean isPresent;

}
