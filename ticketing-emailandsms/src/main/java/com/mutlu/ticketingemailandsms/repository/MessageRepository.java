package com.mutlu.ticketingemailandsms.repository;

import com.mutlu.ticketingemailandsms.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {

}