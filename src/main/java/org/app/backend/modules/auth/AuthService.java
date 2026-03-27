package org.app.backend.modules.auth;

import java.util.UUID;
import org.app.backend.modules.auth.dto.*;

public interface AuthService {
  TokenDTO signIn(SignInDTO dto);

  void signUp(SignUpDTO dto);

  TokenDTO refresh(RefreshTokenDTO dto);

  void signOut(UUID userId);

  void changePassword(UUID userId, ChangePasswordDTO dto);

  void forgotPassword(ForgotPasswordDTO dto);

  void resetPassword(ResetPasswordDTO dto);
}
