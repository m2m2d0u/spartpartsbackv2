package sn.symmetry.spareparts.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.password-reset")
public class PasswordResetProperties {

    private long tokenExpiration = 1800000; // 30 minutes in ms

    private String baseUrl = "http://localhost:3000/reset-password";
}
