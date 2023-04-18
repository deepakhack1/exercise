package com.domain.exercise.resources;

import com.domain.exercise.entity.EmployeeEntity;
import com.domain.exercise.service.EventStoreService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
public class TestEventStoreExit {
    @InjectMocks
    EventStoreExit eventStoreExit;
    @Mock
    EventStoreService eventStoreService;
    private MockMvc mockMvc;
    Optional<EmployeeEntity> optionalEmployeeEntity;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(eventStoreExit).build();
    }

    @Test
    public void testReEntryOfEmplInSameDay() throws Exception {
        optionalEmployeeEntity = Optional.of(EmployeeEntity.builder()
                .isAlreadyEntered(true)
                .name("shiva")
                .id(147)
                .entryTime(LocalDateTime.now())
                .build());
        when(eventStoreService.isEmployeeEntered(anyInt())).thenReturn(optionalEmployeeEntity);
        doNothing().when(eventStoreService).enterExitTime(any(EmployeeEntity.class));
        MvcResult result  = this.mockMvc.perform(get("/eventStore/api/out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("empId","147")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();
        assertNotNull(result.getResponse());
        verify(eventStoreService,times(1)).isEmployeeEntered(anyInt());
        verify(eventStoreService,times(1)).enterExitTime(any(EmployeeEntity.class));
    }

    @Test
    public void testEntryOfEmpWhenAlreadyEntered() throws Exception {
        optionalEmployeeEntity = Optional.of(EmployeeEntity.builder()
                .isAlreadyEntered(false)
                .name("shiva")
                .id(147)
                .entryTime(LocalDateTime.now())
                .build());
        when(eventStoreService.isEmployeeEntered(anyInt())).thenReturn(optionalEmployeeEntity);
        doNothing().when(eventStoreService).enterExitTime(any(EmployeeEntity.class));
        MvcResult result  = this.mockMvc.perform(get("/eventStore/api/out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("empId","147")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()).andReturn();
        assertNotNull(result.getResponse());
        verify(eventStoreService,times(1)).isEmployeeEntered(anyInt());
        verify(eventStoreService,times(0)).enterExitTime(any(EmployeeEntity.class));
    }

}
