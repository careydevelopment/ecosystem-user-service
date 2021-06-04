package com.careydevelopment.ecosystem.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.careydevelopment.ecosystem.user.model.IpLog;

@Repository
public interface IpLogRepository extends MongoRepository<IpLog, String> {

}
