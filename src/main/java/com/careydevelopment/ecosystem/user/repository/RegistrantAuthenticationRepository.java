package com.careydevelopment.ecosystem.user.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.careydevelopment.ecosystem.user.model.RegistrantAuthentication;


@Repository
public interface RegistrantAuthenticationRepository extends MongoRepository<RegistrantAuthentication, String> {

    @Query("{ 'username': '?0', 'time' : { $gte: ?1 }, 'type': '?2', 'code': '?3' }") 
    public List<RegistrantAuthentication> codeCheck(String username, long sinceTime, String type, String code); 
    
    public List<RegistrantAuthentication> findByUsernameOrderByTimeDesc(String username);
    
    public List<RegistrantAuthentication> findByUsernameAndTypeOrderByTimeDesc(String username, String type);
}
