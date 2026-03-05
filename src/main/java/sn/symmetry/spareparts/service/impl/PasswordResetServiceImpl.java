package sn.symmetry.spareparts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.symmetry.spareparts.config.CacheConfig;
import sn.symmetry.spareparts.config.PasswordResetProperties;
import sn.symmetry.spareparts.dto.request.ChangePasswordRequest;
import sn.symmetry.spareparts.dto.request.ForgotPasswordRequest;
import sn.symmetry.spareparts.dto.request.ResetPasswordRequest;
import sn.symmetry.spareparts.entity.PasswordResetToken;
import sn.symmetry.spareparts.entity.User;
import sn.symmetry.spareparts.exception.BusinessRuleException;
import sn.symmetry.spareparts.exception.ResourceNotFoundException;
import sn.symmetry.spareparts.repository.PasswordResetTokenRepository;
import sn.symmetry.spareparts.repository.UserRepository;
import sn.symmetry.spareparts.service.AuthorizationService;
import sn.symmetry.spareparts.service.EmailService;
import sn.symmetry.spareparts.service.PasswordResetService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetProperties passwordResetProperties;
    private final AuthorizationService authorizationService;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        // Silent success for unknown emails (prevents email enumeration)
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();

        // Delete old tokens before creating a new one
        passwordResetTokenRepository.deleteByUserId(user.getId());

        // Generate token
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(passwordResetProperties.getTokenExpiration() / 60000));
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);

        // Send email
        String resetLink = passwordResetProperties.getBaseUrl() + "?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessRuleException("Invalid password reset token"));

        if (Boolean.TRUE.equals(resetToken.getUsed())) {
            throw new BusinessRuleException("Password reset token has already been used");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Password reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.USER_ME_CACHE, allEntries = true)
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = authorizationService.getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPasswordHash())) {
            throw new BusinessRuleException("Current password is incorrect");
        }

        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.USER_ME_CACHE, allEntries = true)
    public String adminResetPassword(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String tempPassword = generateTempPassword(12);
        user.setPasswordHash(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        return tempPassword;
    }

    private String generateTempPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(TEMP_PASSWORD_CHARS.charAt(SECURE_RANDOM.nextInt(TEMP_PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }
}
