package com.careydevelopment.ecosystem.user.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "#{@environment.getProperty('mongo.user.collection')}")
public class User implements UserDetails {

	private static final long serialVersionUID = 3592549577903104696L;

	private String id;
	
	@NotBlank(message = "Please provide a first name")
	@Size(max = 50, message = "Please enter a first name between 1 and 50 characters")
	private String firstName;
	
	@NotBlank(message = "Please provide a last name")
	@Size(max = 50, message = "Please enter a last name between 1 and 50 characters")
	private String lastName;
	
	@NotBlank(message = "Please provide a street address")
	@Size(max = 80, message = "Please enter a street address between 1 and 80 characters")
	private String street1;
	
	@Size(max = 80, message = "Please enter a street address 2 between 1 and 80 characters")
	private String street2;
	
	@NotBlank(message = "Please provide a city")
	@Size(max = 50, message = "Please enter a city between 1 and 50 characters")
	private String city;
	
	@NotBlank(message = "Please provide a state")
	@Size(max = 2, message = "Please enter a two-letter abbreviation for the state")
	private String state;
	
	@NotBlank(message = "Please provide a zip code")
	@Size(max = 15, message = "Please enter a zip code that does not exceed 15 characters")
	private String zip;
	
	@NotBlank(message = "Please provide an email address")
	@Email(message = "Please provide a valid email address")
	private String email;
	
	@NotBlank(message = "Please provide a phone number")
	@Size(max = 15, message = "Please enter a phone number that does not exceed 15 characters")
	private String phoneNumber;
	
	@JsonIgnore
	private List<String> authorityNames = new ArrayList<String>();
	
	@NotBlank(message = "Please provide a username")
	@Size(max = 20, message = "Please enter a username that does not exceed 20 characters")
	private String username;
	
	@NotBlank(message = "Please provide a country")
	@Size(max = 2, message = "Please enter a two-digit abbreviation for country")
	private String country;
	
	@JsonIgnore
	private String password;	
	
	@JsonIgnore
	private GoogleApi googleApi;

	private EmailIntegration emailIntegration;
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getStreet1() {
		return street1;
	}
	public void setStreet1(String street1) {
		this.street1 = street1;
	}
	public String getStreet2() {
		return street2;
	}
	public void setStreet2(String street2) {
		this.street2 = street2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getAuthorityNames() {
		return authorityNames;
	}
	public void setAuthorityNames(List<String> authorityNames) {
		this.authorityNames = authorityNames;
	}
	
	public GoogleApi getGoogleApi() {
        return googleApi;
    }
    public void setGoogleApi(GoogleApi googleApi) {
        this.googleApi = googleApi;
    }
    public EmailIntegration getEmailIntegration() {
        return emailIntegration;
    }
    public void setEmailIntegration(EmailIntegration emailIntegration) {
        this.emailIntegration = emailIntegration;
    }
    
    @Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}
	
	
	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return true;
	}
	
	
	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	
	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return true;
	}
	
	
	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> list = authorityNames
				.stream()
				.map(auth -> new SimpleGrantedAuthority(auth))
				.collect(Collectors.toList());

		return list;
	}
	
	
	public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
	
	
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
