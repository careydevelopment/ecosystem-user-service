package com.careydevelopment.ecosystem.user.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.careydevelopment.ecosystem.user.model.IpLog;
import com.careydevelopment.ecosystem.user.repository.IpLogRepository;

import us.careydevelopment.ecosystem.jwt.model.IpTracker;

@Service
public class IpLogService implements IpTracker {
    
    private static final Logger LOG = LoggerFactory.getLogger(IpLogService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IpLogRepository ipLogRepository;
    
    
    @Override
    public List<IpLog> fetchIpFailureRecord(String ipAddress, Long startingTime) {
        List<IpLog> list = new ArrayList<>();
        List<AggregationOperation> ops = new ArrayList<>();
        
        if (ipAddress != null) {
            AggregationOperation ipMatch = Aggregation.match(Criteria.where("ipAddress").is(ipAddress));
            ops.add(ipMatch);
            
            AggregationOperation dateThreshold = Aggregation.match(Criteria.where("lastLoginAttempt").gte(startingTime));
            ops.add(dateThreshold);

            AggregationOperation failMatch = Aggregation.match(Criteria.where("successfulLogin").is(false));
            ops.add(failMatch);
            
            Aggregation aggregation = Aggregation.newAggregation(ops);
            
            list = mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(IpLog.class), IpLog.class).getMappedResults();
        }        
        
        return list;
    }


    @Override
    public void successfulLogin(String username, String ipAddress) {
        IpLog ipLog = new IpLog();
        
        ipLog.setIpAddress(ipAddress);
        ipLog.setLastLoginAttempt(System.currentTimeMillis());
        ipLog.setSuccessfulLogin(true);
        ipLog.setUsername(username);
        
        ipLogRepository.save(ipLog);
    }
    
    
    @Override
    public void unsuccessfulLogin(String username, String ipAddress) {
        IpLog ipLog = new IpLog();
        
        ipLog.setIpAddress(ipAddress);
        ipLog.setLastLoginAttempt(System.currentTimeMillis());
        ipLog.setSuccessfulLogin(false);
        ipLog.setUsername(username);
        
        ipLogRepository.save(ipLog);
    }
    
}
