package com.domain.exercise.service;

import com.domain.exercise.model.KafkaPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class KafkaProducer {


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private static final String topic = "attendanceTopic";

    public void sendMessage(KafkaPayload kafkaPayload) {

        String message = null;
        try {
            message = convertStringToEmployee(kafkaPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ListenableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topic,message);
        try {
             RecordMetadata recordMetadata = sendResult.get().getRecordMetadata();
             String topic = recordMetadata.topic();
             long offset = recordMetadata.offset();
             log.info("send data to topic {} , offset: {}",topic,offset);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private  String convertStringToEmployee(KafkaPayload kafkaPayload) throws JsonProcessingException {
        return objectMapper.writeValueAsString(kafkaPayload);
    }

}