package com.co.ias.reactive.webflux.services;

import com.co.ias.reactive.webflux.model.Topic;
import com.co.ias.reactive.webflux.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.print.attribute.standard.Media;

@Component
public class TopicHandler {
    @Autowired
    private TopicRepository topicRepository;

    public Mono<ServerResponse> createTopic(ServerRequest request){
        return request
                .bodyToMono(Topic.class)
                .flatMap(topicRepository::save)
                .flatMap(savedTopic->ServerResponse.status(HttpStatus.CREATED).bodyValue(savedTopic))
                .doOnNext(System.out::println);
    }

    public Mono<ServerResponse> updateTopic(ServerRequest request){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(topicRepository
                        .findById(Integer.valueOf(request.pathVariable("id")))
                        .flatMap(topic->request
                                .bodyToMono(Topic.class)
                                .flatMap(topicRequest -> {
                                    Topic updatedTopic =
                                            Topic
                                                    .builder()
                                                    .id(topic.getId())
                                                    .name(topicRequest.getName())
                                                    .description(topicRequest.getDescription())
                                                    .build();
                                    return topicRepository.save(updatedTopic);
                                })),Topic.class)
                .switchIfEmpty(ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("Not topic found for update"));
    }
    public Mono<ServerResponse> readTopics(ServerRequest request){
        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body( topicRepository.findAll(),Topic.class)
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).bodyValue("No hay registros."));

    }
    public Mono<ServerResponse> readTopic(ServerRequest request){
        return topicRepository.findById(Integer.valueOf(request.pathVariable("id")))
                .flatMap(topic-> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(topic))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT)
                        .bodyValue("No content"));
    }
    public Mono<ServerResponse> deleteTopic(ServerRequest request){
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(topicRepository.deleteById(Integer.valueOf(request.pathVariable("id"))),Topic.class)
                .onErrorResume(e -> ServerResponse.status(HttpStatus.PRECONDITION_FAILED)
                        .bodyValue(e.getMessage()));
    }

}
