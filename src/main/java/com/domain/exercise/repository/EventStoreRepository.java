package com.domain.exercise.repository;

import com.domain.exercise.entity.EmployeeEnity;
import com.domain.exercise.model.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import java.util.Optional;
@Repository
public interface EventStoreRepository extends CrudRepository<EmployeeEnity,Integer> {

    List<Optional<Employee>> findByName(String name);

    boolean findByEntryTime(LocalDateTime localDateTime);

}
