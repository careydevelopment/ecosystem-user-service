package com.careydevelopment.ecosystem.user.util;

import com.careydevelopment.ecosystem.user.model.User;

import us.careydevelopment.ecosystem.file.FileNameUtil;

public class UserFileNameUtil extends FileNameUtil {

    public static String createFileName(User user, String originalFileName) {
        String fileName = createTimestampedUniqueFileName(user.getId());

        fileName = appendExtensionFromOriginalFileName(fileName, originalFileName);

        return fileName;
    }
}
