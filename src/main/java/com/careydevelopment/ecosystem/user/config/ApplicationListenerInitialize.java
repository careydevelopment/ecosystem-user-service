package com.careydevelopment.ecosystem.user.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.RegistrantAuthentication;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.model.UserSearchCriteria;
import com.careydevelopment.ecosystem.user.repository.IpLogRepository;
import com.careydevelopment.ecosystem.user.repository.RegistrantAuthenticationRepository;
import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.service.RegistrantService;
import com.careydevelopment.ecosystem.user.service.SmsService;
import com.careydevelopment.ecosystem.user.service.UserService;
import com.careydevelopment.ecosystem.user.util.TotpUtil;

import us.careydevelopment.ecosystem.jwt.constants.Authority;

@Component
public class ApplicationListenerInitialize implements ApplicationListener<ApplicationReadyEvent>  {
	
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationListenerInitialize.class); 

    
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;
	
    @Autowired
    UserService userService;
    
    @Autowired
    TotpUtil totpUtil;
    
    @Autowired
    IpLogRepository ipLogRepository;
    
    @Autowired
    RegistrantService registrantService;
    
    @Autowired
    RegistrantAuthenticationRepository registrantAuthenticationRepository;
    
    @Autowired
    SmsService smsService;
    
    
    public void onApplicationEvent(ApplicationReadyEvent event) {
        
        //smsService.cancelRequest("eb8d98dece1b462f98ab3a6f089edda3");
                
        //smsService.checkValidationCode("a0bdc643a3614d3e86ae268a6b80a7a8", "0402");
        
//        List<RegistrantAuthentication> list = this.registrantAuthenticationRepository.findAll();
//        list.forEach(auth -> {
//            System.err.println(auth);
//        });
    
        
//        List<RegistrantAuthentication> list = registrantService.validateEmailCode("jobab", "635545");
//        System.err.println(list);
        
//        

        //System.err.println(totpUtil.getTOTPCode());
    
//        List<IpLog> list = ipLogRepository.findAll();
//        list.forEach(ip -> {
//            System.err.println(ip);
//        });
        

//      Registrant registrant = new Registrant();
//      registrant.setUsername("jobab");
//      registrant.setEmailAddress("mrbrianmcarey@gmail.com");
//      
//      registrantService.createTextCode(registrant);

//      List<RegistrantAuthentication> list = registrantAuthenticationRepository.findByUsernameAndTypeOrderByTimeDesc("jobab", RegistrantAuthentication.Type.TEXT.toString());
//      list.forEach(auth -> {
//          System.err.println(auth);          
//      });
        
        
        
        //userRepository.save(user);
      UserSearchCriteria criteria = new UserSearchCriteria();
      criteria.setUsername("bloonie");
      List<User> users = userService.search(criteria);
      System.err.println(users);

        if (users.size() > 0) {
            users.get(0).setCountry("US");
            //this.userRepository.save(users.get(0));
            //userRepository.delete(users.get(0));
            //registrantService.addAuthority("jobab", Authority.BASIC_ECOSYSTEM_USER);
        }
    }
}