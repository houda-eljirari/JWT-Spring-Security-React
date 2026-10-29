
package ma.enset.incident_management.config;

import ma.enset.incident_management.entity.Role;
import ma.enset.incident_management.entity.User;
import ma.enset.incident_management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DevDataInitializer {

    @Bean
    CommandLineRunner initializeTestUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            createUserIfAbsent(
                    userRepository, passwordEncoder,
                    "admin_test", "admin_test@example.com",
                    "AdminTest123!", Role.ADMIN);

            createUserIfAbsent(
                    userRepository, passwordEncoder,
                    "technicien_test", "technicien_test@example.com",
                    "TechnicienTest123!", Role.TECHNICIAN);
        };
    }

    private void createUserIfAbsent(
            UserRepository repository,
            PasswordEncoder encoder,
            String username,
            String email,
            String rawPassword,
            Role role) {

        if (repository.existsByUsername(username)) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encoder.encode(rawPassword));
        user.setRole(role);
        repository.save(user);
    }
}
