package com.careydevelopment.ecosystem.user.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.careydevelopment.ecosystem.user.exception.UserNotFoundException;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.model.UserSearchCriteria;
import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.service.UserService;
import com.careydevelopment.ecosystem.user.util.SessionUtil;
import com.careydevelopment.ecosystem.user.util.UserFileUtil;
import com.careydevelopment.ecosystem.user.util.UserUtil;

import us.careydevelopment.ecosystem.file.exception.FileTooLargeException;
import us.careydevelopment.ecosystem.file.exception.MissingFileException;
import us.careydevelopment.ecosystem.jwt.constants.CookieConstants;
import us.careydevelopment.util.api.cookie.CookieUtil;
import us.careydevelopment.util.api.input.InputSanitizer;
import us.careydevelopment.util.api.model.IRestResponse;
import us.careydevelopment.util.api.response.ResponseEntityUtil;

@RestController
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserFileUtil fileUtil;

    @Autowired
    private SessionUtil sessionUtil;
    
    @Autowired
    private UserUtil userUtil;
    

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<IRestResponse<Void>> userNotFound(UserNotFoundException ex) {
        return ResponseEntityUtil.createResponseEntityWithError(ex.getMessage(),
                HttpStatus.NOT_FOUND.value());
    }
    
    @GetMapping("/{userId}/profileImage")
    public ResponseEntity<ByteArrayResource> getProfileImage(@PathVariable String userId) {
        try { 
            Path imagePath = fileUtil.fetchProfilePhotoByUserId(userId);

            if (imagePath != null) {
                LOG.debug("Getting image from " + imagePath.toString());

                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(imagePath));

                return ResponseEntity.ok().contentLength(imagePath.toFile().length()).contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                LOG.debug("Profile photo not found for user " + userId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/profileImage")
    public ResponseEntity<IRestResponse<Void>> saveProfileImage(@RequestParam("file") MultipartFile file) {
        User user = sessionUtil.getCurrentUser();
        LOG.debug("User uploading is " + user);

        //TODO: Use exception handler here
        try {
            fileUtil.saveProfilePhoto(file, user);

            return ResponseEntityUtil.createSuccessfulResponseEntity("Profile image created successfully!", HttpStatus.CREATED.value());
        } catch (FileTooLargeException fe) {
            return ResponseEntityUtil.createResponseEntityWithError("File too large", HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (MissingFileException me) {
            return ResponseEntityUtil.createResponseEntityWithError("Missing file", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseEntityUtil.createResponseEntityWithError("Unexpected problem", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @Valid @RequestBody User user,
            BindingResult bindingResult) {
        
        //ensure id in URL matches id in body
        user.setId(userId);
        LOG.debug("updated user data is " + user);        

        userUtil.validateUserUpdate(user, bindingResult);
        
        InputSanitizer.sanitizeBasic(user);

        User updatedUser = userService.updateUser(user);
        LOG.debug("updated user is " + updatedUser);

        return ResponseEntityUtil.createSuccessfulResponseEntity("User updated successfully", HttpStatus.OK.value(), updatedUser);    
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        LOG.debug("deleting user " + userId);
        User user = userUtil.validateUserDelete(userId);
        
        userRepository.delete(user);
        
        return ResponseEntityUtil.createSuccessfulResponseEntity("User successfully deleted", HttpStatus.NO_CONTENT.value());
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        try {
            User user = sessionUtil.getCurrentUser();
            return ResponseEntityUtil.createSuccessfulResponseEntity("User successfully retrieved", HttpStatus.OK.value(), user);
        } catch (Exception e) {
            LOG.error("Problem retrieving current user!", e);
            
            //intentionally vague here for security reasons
            return ResponseEntityUtil.createResponseEntityWithError("User not found", HttpStatus.NOT_FOUND.value());
        }
    }

    @DeleteMapping("/session")
    public ResponseEntity<?> logout(
            @CookieValue(name = CookieConstants.ACCESS_TOKEN_COOKIE_NAME, required = false) String jwtToken,
            HttpServletResponse response) {

        if (jwtToken != null) {
            CookieUtil.expireCookie(CookieConstants.ACCESS_TOKEN_COOKIE_NAME, response);
        }

        return ResponseEntityUtil.createSuccessfulResponseEntity("User successfully logged out", HttpStatus.OK.value());
    }

    @GetMapping("/simpleSearch")
    public ResponseEntity<?> search(@RequestParam(required = false) String emailAddress,
            @RequestParam(required = false) String username) {
        UserSearchCriteria searchCriteria = new UserSearchCriteria();
        searchCriteria.setEmailAddress(emailAddress);
        searchCriteria.setUsername(username);

        LOG.debug("Search criteria is " + searchCriteria);

        List<User> users = userService.search(searchCriteria);
        LOG.debug("Returning users " + users);
        
        return ResponseEntityUtil.createSuccessfulResponseEntity("Successful query", HttpStatus.OK.value(), users);
    }
}