package zw.co.learniverse.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import zw.co.learniverse.entities.User;
import zw.co.learniverse.enums.Role;
import zw.co.learniverse.repository.UserRepository;
import zw.co.learniverse.service.impl.AuthenticationServiceImpl;
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationServiceImpl authenticationServiceImpl;

    @Override
    public void run(String... args) throws Exception {
        // Create super admin user
        createSuperAdmin();

    }

    private void createSuperAdmin() {
        if (!userRepository.findByEmail("superadmin@learniverse.co.zw").isPresent()) {
            User superAdmin = new User();
            superAdmin.setFirstname("Super");
            superAdmin.setLastname("Admin");
            superAdmin.setEmail("superadmin@learniverse.co.zw");
            superAdmin.setSchoolId(0L);
            superAdmin.setAccessNumber("SA123456789");
            superAdmin.setPassword(passwordEncoder.encode("Password@123"));
            superAdmin.setRole(Role.ADMIN);
            superAdmin.setTemporaryPassword(false);

            userRepository.save(superAdmin);
            System.out.println("Default SUPER ADMIN user created.");
        } else {
            System.out.println("SUPER ADMIN user already exists.");
        }
    }

}
