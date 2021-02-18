package com.kedacom.haiou.kmtool.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2021/1/28.
 */
@EnableKafka
@Configuration
public class KafkaKedaConfig {

    @Value("${spring.kafka.keda.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.keda.group-id}")
    private String groupId;

    @Value("${spring.kafka.keda.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.keda.producer.value-serializer}")
    private String valueSerializer;

    @Value("${spring.kafka.keda.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${spring.kafka.keda.consumer.value-deserializer}")
    private String valueDeserializer;



    @Bean(name = "kafkaKedaTemplate")
    public KafkaTemplate<String, String> kafkaKedaTemplate(){
        return new KafkaTemplate<String, String>(producerFactory());
    }

    @Bean(name = "kafkaKedaContainerFactory")
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaKedaContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    private ConsumerFactory<? super Integer,? super String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<Integer, String>(consumerConfigs());
    }

    private ProducerFactory<String,String> producerFactory() {
        return new DefaultKafkaProducerFactory<String, String>(producerConfigs());
    }

    private Map<String,Object> producerConfigs() {
        Map<String, Object> propConfigs = new HashMap<>();
        propConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        propConfigs.put(ProducerConfig.RETRIES_CONFIG, 10);
        propConfigs.put(ProducerConfig.ACKS_CONFIG, "all");
        propConfigs.put(ProducerConfig.BATCH_SIZE_CONFIG, 1000);
        propConfigs.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        propConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        propConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        return propConfigs;
    }

    private Map<String,Object> consumerConfigs() {
        Map<String, Object> consConfigs = new HashMap<>();
        consConfigs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consConfigs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        consConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        consConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        consConfigs.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        return consConfigs;
    }

}
