package org.app.backend.modules.auth.security;

import java.security.SecureRandom;
import java.util.Base64;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthUtils {
  SecureRandom SECURE_RANDOM = new SecureRandom();

  public String generateToken(int byteLength) {
    byte[] randomBytes = new byte[byteLength];
    SECURE_RANDOM.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

  public String hashToken(String refreshToken) {
    return DigestUtils.sha256Hex(refreshToken);
  }
}
