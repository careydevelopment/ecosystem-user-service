package com.careydevelopment.ecosystem.user.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.careydevelopment.ecosystem.user.model.User;

import us.careydevelopment.ecosystem.file.FileUtil;
import us.careydevelopment.ecosystem.file.exception.CopyFileException;
import us.careydevelopment.ecosystem.file.exception.FileTooLargeException;
import us.careydevelopment.ecosystem.file.exception.ImageRetrievalException;
import us.careydevelopment.ecosystem.file.exception.MissingFileException;

@Component
public class UserFileUtil extends FileUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    public UserFileUtil(@Value("${user.files.base.path}") String userFilesBasePath,
            @Value("${max.file.upload.size}") Long maxFileUploadSize) {
        this.maxFileUploadSize = maxFileUploadSize;
        this.userFilesBasePath = userFilesBasePath;
    }

    public Path fetchProfilePhotoByUserId(String userId) throws ImageRetrievalException {
        Path imagePath = null;

        Path rootLocation = Paths.get(getRootLocationForUserProfileImageUpload(userId));
        LOG.debug("Fetching profile image from " + rootLocation.toString());

        try {
            if (rootLocation.toFile().exists()) {
                Iterator<Path> iterator = Files.newDirectoryStream(rootLocation).iterator();

                if (iterator.hasNext()) {
                    imagePath = iterator.next();
                    LOG.debug("File name is " + imagePath);
                }
            }
        } catch (IOException ie) {
            throw new ImageRetrievalException(ie.getMessage());
        }

        return imagePath;
    }

    public void saveProfilePhoto(MultipartFile file, User user)
            throws MissingFileException, FileTooLargeException, CopyFileException {
        validateFile(file, maxFileUploadSize);
        Path rootLocation = Paths.get(getRootLocationForUserProfileImageUpload(user));
        deleteAllFilesInDirectory(rootLocation);
        saveFile(file, user, rootLocation);
    }

    private void saveFile(MultipartFile file, User user, Path rootLocation) throws CopyFileException {
        try (InputStream is = file.getInputStream()) {
            String newFileName = getNewFileName(file, user);
            Files.copy(is, rootLocation.resolve(newFileName));
        } catch (IOException ie) {
            LOG.error("Problem uploading file!", ie);
            throw new CopyFileException("Failed to upload!");
        }
    }

    private String getNewFileName(MultipartFile file, User user) {
        LOG.debug("File name is " + file.getOriginalFilename());

        String newFileName = UserFileNameUtil.createFileName(user, file.getOriginalFilename());
        LOG.debug("New file name is " + newFileName);

        return newFileName;
    }

    public String getRootLocationForUserUpload(User user) {
        if (user == null)
            throw new IllegalArgumentException("No user provided!");
        return this.getRootLocationForUserUpload(user.getId());
    }

    public String getRootLocationForUserProfileImageUpload(String userId) {
        if (StringUtils.isEmpty(userId))
            throw new IllegalArgumentException("No user id!");

        String base = getRootLocationForUserUpload(userId);

        StringBuilder builder = new StringBuilder(base);
        builder.append("/");
        builder.append(PROFILE_DIR);

        String location = builder.toString();

        createDirectoryIfItDoesntExist(location);

        return location;
    }

    public String getRootLocationForUserProfileImageUpload(User user) {
        if (user == null)
            throw new IllegalArgumentException("No user provided!");
        return this.getRootLocationForUserProfileImageUpload(user.getId());
    }
}
