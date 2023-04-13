package com.domain.exercise.resources;

import com.domain.exercise.entity.EmployeeEntity;
import com.domain.exercise.model.Employee;
import com.domain.exercise.service.EventStoreService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
public class TestEventStoreEntry {

    @InjectMocks
    EventStoreEntry eventStoreEntry;
    @Mock
    EventStoreService eventStoreService;

    private MockMvc mockMvc;

    Optional<EmployeeEntity> optionalEmployeeEntity;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(eventStoreEntry).build();
    }

    @Test
    public void testReEntryOfEmplInSameDay() throws Exception {
        optionalEmployeeEntity = Optional.of(EmployeeEntity.builder()
                .isAlreadyEntered(true)
                .name("shiva")
                .id(147)
                .entryTime(LocalDateTime.now())
                .isAlreadyEntered(false)
                .build());
        when(eventStoreService.verifyEmployeeEntry(anyInt())).thenReturn(optionalEmployeeEntity);
        doNothing().when(eventStoreService).empEntryAfterFirstTime(any(EmployeeEntity.class));
        MvcResult result  = this.mockMvc.perform(post("/eventStore/api/in")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(getJsonString(new Employee(147,"shiva")))
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().is2xxSuccessful()).andReturn();
        assertNotNull(result.getResponse());
        verify(eventStoreService,times(1)).verifyEmployeeEntry(anyInt());
        verify(eventStoreService,times(1)).empEntryAfterFirstTime(any(EmployeeEntity.class));
        verify(eventStoreService,times(0)).empEntry(any(Employee.class));
    }

    @Test
    public void testFirstEntryInADay() throws Exception {
        optionalEmployeeEntity = Optional.of(EmployeeEntity.builder()
                .isAlreadyEntered(true)
                .name("shiva")
                .id(147)
                .entryTime(LocalDateTime.now().minusDays(3))
                .isAlreadyEntered(false)
                .build());
        when(eventStoreService.verifyEmployeeEntry(anyInt())).thenReturn(optionalEmployeeEntity);
        doNothing().when(eventStoreService).empEntryAfterFirstTime(any(EmployeeEntity.class));
        MvcResult result  = this.mockMvc.perform(post("/eventStore/api/in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(new Employee(147,"shiva")))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();
        assertNotNull(result.getResponse());
        verify(eventStoreService,times(1)).verifyEmployeeEntry(anyInt());
        verify(eventStoreService,times(0)).empEntryAfterFirstTime(any(EmployeeEntity.class));
        verify(eventStoreService,times(1)).empEntry(any(Employee.class));
    }

    private String getJsonString(Employee employee) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(employee);
    }
}
