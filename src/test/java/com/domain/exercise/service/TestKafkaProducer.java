package com.domain.exercise.service;

import com.domain.exercise.model.KafkaPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class TestKafkaProducer {

    @InjectMocks
    KafkaProducer kafkaProducer;
    @Mock
    ObjectMapper mockObjectMapper;
    @Mock
    KafkaTemplate<String, String> mockKafkaTemplate;
    @Mock
    SendResult<String, String> sendResult;
    @Mock
    ListenableFuture<SendResult<String, String>> responseFuture;



    @BeforeEach
    void setUp(){
        openMocks(this);
    }

    @Test
    void testSendMessage() throws JsonProcessingException, ExecutionException, InterruptedException {
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("attendanceTopic", 2), 1, 0L, 0L, 0L, 0, 0);
        when(responseFuture.get()).thenReturn(sendResult);
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(mockKafkaTemplate.send("attendanceTopic", "dummyMessage")).thenReturn(responseFuture);
        when(mockObjectMapper.writeValueAsString(any(KafkaPayload.class))).thenReturn("dummyMessage");
        doAnswer(invocationOnMock -> {
            ListenableFutureCallback listenableFutureCallback = invocationOnMock.getArgument(0);
            listenableFutureCallback.onSuccess(sendResult);
            assertEquals(sendResult.getRecordMetadata().offset(), 1);
            assertEquals(sendResult.getRecordMetadata().partition(), 2);
            return null;
        }).when(responseFuture).addCallback(any(ListenableFutureCallback.class));
        kafkaProducer.sendMessage(KafkaPayload.builder().attendance(1.0D).name("testName").empId(14311).build());
        verify(mockKafkaTemplate, times(1)).send("attendanceTopic", "dummyMessage");

    }

}
