package com.domain.exercise.repository;

import com.domain.exercise.entity.EmployeeEntity;
import com.domain.exercise.model.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import java.util.Optional;
@Repository
public interface EventStoreRepository extends CrudRepository<EmployeeEntity,Integer> {

//    List<Optional<EmployeeEntity>> findByName(String name);

//    boolean findByEntryTime(LocalDateTime localDateTime);

}
