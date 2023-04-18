package com.domain.exercise.service;

import com.domain.exercise.entity.EmployeeEntity;
import com.domain.exercise.model.Employee;
import com.domain.exercise.model.KafkaPayload;
import com.domain.exercise.repository.EventStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class TestEventStoreServiceImpl {

    @InjectMocks
    EventStoreServiceImpl eventStoreService;
    @Mock
    EventStoreRepository eventStoreRepository;
    @Mock
    KafkaProducer kafkaProducer;
    @Captor
    ArgumentCaptor<EmployeeEntity> empEntityArgumentCaptor1;
    @Captor
    ArgumentCaptor<EmployeeEntity> empEntityArgumentCaptor2;
    @Captor
    ArgumentCaptor<EmployeeEntity> empEntityArgumentCaptor3;
    @Captor
    ArgumentCaptor<KafkaPayload> kafkaPayloadArgumentCaptor;

    @BeforeEach
    void setUp(){
        openMocks(this);
    }

    @Test
    void testEnterExitTime(){
        LocalDateTime entryTime = LocalDateTime.now();
        EmployeeEntity empEntity = EmployeeEntity.builder().isAlreadyEntered(false)
                                                           .id(14331)
                                                           .name("testName")
                                                           .entryTime(entryTime)
                                                           .build();
        when(eventStoreRepository.save(any(EmployeeEntity.class))).thenReturn(empEntity);
        eventStoreService.enterExitTime(empEntity);
        verify(eventStoreRepository,times(1)).save(empEntityArgumentCaptor1.capture());
        EmployeeEntity expected = empEntityArgumentCaptor1.getValue();
        assertEquals(14331,expected.getId());
        assertEquals("testName",expected.getName());
        assertEquals(entryTime,expected.getEntryTime());
        assertFalse(expected.isAlreadyEntered());
    }

    @Test
    void testempEntryAfterFirstTime(){
        LocalDateTime entryTime = LocalDateTime.now();
        EmployeeEntity empEntity = EmployeeEntity.builder().isAlreadyEntered(true)
                .id(14331)
                .name("testName")
                .entryTime(entryTime)
                .build();
        when(eventStoreRepository.save(any(EmployeeEntity.class))).thenReturn(empEntity);
        eventStoreService.empEntryAfterFirstTime(empEntity);
        verify(eventStoreRepository,times(1)).save(empEntityArgumentCaptor2.capture());
        EmployeeEntity expected = empEntityArgumentCaptor2.getValue();
        assertEquals(14331,expected.getId());
        assertEquals("testName",expected.getName());
        assertEquals(entryTime,expected.getEntryTime());
        assertTrue(expected.isAlreadyEntered());
    }

    @Test
    void tesCalculateAttendance(){
        LocalDateTime entryTime = LocalDateTime.now();
        LocalDateTime exitTime = LocalDateTime.now().plusHours(1);
        EmployeeEntity empEntity = EmployeeEntity.builder().isAlreadyEntered(true)
                .id(14331)
                .name("testName")
                .entryTime(entryTime)
                .exitTime(exitTime)
                .build();
        when(eventStoreRepository.findById(any(Integer.class))).thenReturn(Optional.of(empEntity));
        doNothing().when(kafkaProducer).sendMessage(any(KafkaPayload.class));
        double actualHours = eventStoreService.calculateAttendance(14331);
        verify(kafkaProducer,times(1)).sendMessage(kafkaPayloadArgumentCaptor.capture());
        KafkaPayload actualKafkaPayLoad = kafkaPayloadArgumentCaptor.getValue();
        assertEquals("testName",actualKafkaPayLoad.getName());
        assertEquals(14331,actualKafkaPayLoad.getEmpId());
        assertEquals(1.0,actualKafkaPayLoad.getAttendance());
        assertEquals(1.0,actualHours);

    }
    @Test
    void testEmpEntry(){
        LocalDateTime entryTime = LocalDateTime.now();
        Employee emp = Employee.builder()
                .empId(14331)
                .empName("testName")
                .build();
        when(eventStoreRepository.save(any(EmployeeEntity.class))).thenReturn(new EmployeeEntity());
        eventStoreService.empEntry(emp);
        verify(eventStoreRepository,times(1)).save(empEntityArgumentCaptor3.capture());
        EmployeeEntity expected = empEntityArgumentCaptor3.getValue();
        assertEquals(14331,expected.getId());
        assertEquals("testName",expected.getName());
        assertTrue(expected.isAlreadyEntered());
    }
}
