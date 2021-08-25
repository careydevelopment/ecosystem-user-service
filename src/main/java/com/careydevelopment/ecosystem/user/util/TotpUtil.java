package com.careydevelopment.ecosystem.user.util;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.taimos.totp.TOTP;

@Component
public class TotpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(TotpUtil.class);

    
    @Value("${tfa.secret.key}")
    private String secretKey;
    

    public String getTOTPCode() {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }
}
