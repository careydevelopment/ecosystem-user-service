package com.careydevelopment.ecosystem.user.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "#{@environment.getProperty('mongo.registrant-authentication.collection')}")
public class RegistrantAuthentication {

    public enum Type { EMAIL, TEXT };
    
    private Registrant registrant;
    private Long time;
    private Type type;
    private String code;
    
    
    public Registrant getRegistrant() {
        return registrant;
    }
    public void setRegistrant(Registrant registrant) {
        this.registrant = registrant;
    }
    public Long getTime() {
        return time;
    }
    public void setTime(Long time) {
        this.time = time;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
