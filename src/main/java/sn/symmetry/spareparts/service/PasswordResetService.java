package sn.symmetry.spareparts.service;

import sn.symmetry.spareparts.dto.request.ChangePasswordRequest;
import sn.symmetry.spareparts.dto.request.ForgotPasswordRequest;
import sn.symmetry.spareparts.dto.request.ResetPasswordRequest;

import java.util.UUID;

public interface PasswordResetService {

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request);

    String adminResetPassword(UUID userId);
}
