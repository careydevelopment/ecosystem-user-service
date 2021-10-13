package com.careydevelopment.ecosystem.user.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.servlet.http.Cookie;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.model.UserSearchCriteria;
import com.careydevelopment.ecosystem.user.service.UserService;
import com.careydevelopment.ecosystem.user.util.SecurityUtil;
import com.careydevelopment.ecosystem.user.util.UserFileUtil;

import us.careydevelopment.ecosystem.file.exception.FileTooLargeException;
import us.careydevelopment.ecosystem.file.exception.MissingFileException;
import us.careydevelopment.ecosystem.jwt.constants.CookieConstants;
import us.careydevelopment.util.api.input.InputSanitizer;

@RestController
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserFileUtil fileUtil;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping("/{userId}/profileImage")
    public ResponseEntity<?> getProfileImage(@PathVariable String userId) {
        try {
            Path imagePath = fileUtil.fetchProfilePhotoByUserId(userId);

            if (imagePath != null) {
                LOG.debug("Getting image from " + imagePath.toString());

                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(imagePath));

                return ResponseEntity.ok().contentLength(imagePath.toFile().length()).contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                LOG.debug("Profile photo not found for user " + userId);
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/profileImage")
    public ResponseEntity<?> saveProfileImage(@RequestParam("file") MultipartFile file) {
        User user = securityUtil.getCurrentUser();
        LOG.debug("User uploading is " + user);

        try {
            fileUtil.saveProfilePhoto(file, user);

            return ResponseEntity.ok().build();
        } catch (FileTooLargeException fe) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        } catch (MissingFileException me) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @Valid @RequestBody User user,
            BindingResult bindingResult) {
        boolean allowed = securityUtil.isAuthorizedByUserId(userId);
        LOG.debug("updated user data is " + user);

        if (allowed) {
            if (bindingResult.hasErrors()) {
                LOG.debug("Binding result: " + bindingResult);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
            } else {
                InputSanitizer.sanitizeBasic(user);

                User updatedUser = userService.updateUser(user);
                LOG.debug("updated user is " + updatedUser);

                return ResponseEntity.ok(updatedUser);
            }
        } else {
            LOG.debug("Not allowed to update user ID " + userId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        try {
            User user = securityUtil.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            LOG.error("Problem retrieving current user!", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/session")
    public ResponseEntity<?> logout(
            @CookieValue(name = CookieConstants.ACCESS_TOKEN_COOKIE_NAME, required = false) String jwtToken,
            HttpServletResponse response) {

        if (jwtToken != null) {
            expireCookie(response);
        }

        return ResponseEntity.ok().build();
    }

    private void expireCookie(HttpServletResponse response) {
        final Cookie cookie = new Cookie(CookieConstants.ACCESS_TOKEN_COOKIE_NAME, "");

        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);

        response.addCookie(cookie);
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

        return ResponseEntity.ok(users);
    }
}