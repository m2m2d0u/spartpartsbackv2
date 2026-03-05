package sn.symmetry.spareparts.service;

public interface EmailService {

    void sendPasswordResetEmail(String to, String resetLink);
}
