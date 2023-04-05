package com.domain.exercise.entity;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "EmployeeEntity")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EmployeeEntity {
    @Id
    @NonNull
    private int id;
    private String name;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private boolean isPresent;

}
