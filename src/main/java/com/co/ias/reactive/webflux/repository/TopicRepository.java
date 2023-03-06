package com.co.ias.reactive.webflux.repository;


import com.co.ias.reactive.webflux.model.Topic;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends ReactiveCrudRepository<Topic,Integer> {

}
