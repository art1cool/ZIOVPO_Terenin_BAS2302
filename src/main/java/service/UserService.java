package service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.UserRepository;
import repository.UserSessionRepository;
import entity.UserEntity;
import entity.UserSessionEntity;
import model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserEntity getUser(String name) {
        UserEntity user = userRepository.findByName(name);
        return user;
    }

    public UserEntity addUser(User userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.findByName(userDto.getName()) != null) {
            throw new RuntimeException("Name already in use");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (userEntity.getRole() == null) {
            userEntity.setRole(enums.UserRole.USER);
        }
        return userRepository.save(userEntity);
    }

    public UserEntity registerUser(User userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.findByName(userDto.getName()) != null) {
            throw new RuntimeException("Name already in use");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (userEntity.getRole() == null) {
            userEntity.setRole(enums.UserRole.USER);
        }
        return userRepository.save(userEntity);
    }

    @Transactional
    public void removeUser(String name) {
        UserEntity user = userRepository.findByName(name);
        if (user != null) {
            List<UserSessionEntity> sessions = userSessionRepository.findByUserId(user.getId());
            userSessionRepository.deleteAll(sessions);

            userRepository.delete(user);
        }
    }

    public UserEntity updateUser(String name, User updatedFields) {
        UserEntity existing = userRepository.findByName(name);
        if (existing == null) {
            return null;
        }

        if (updatedFields.getName() != null && !updatedFields.getName().isBlank()) {
            existing.setName(updatedFields.getName());
        }
        if (updatedFields.getEmail() != null && !updatedFields.getEmail().isBlank()) {
            existing.setEmail(updatedFields.getEmail());
        }
        if (updatedFields.getPassword() != null && !updatedFields.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedFields.getPassword()));
        }

        return userRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        org.springframework.security.core.userdetails.User.UserBuilder builder =
                org.springframework.security.core.userdetails.User.withUsername(userEntity.getEmail());

        builder.password(userEntity.getPassword());
        builder.authorities(userEntity.getRole().getGrantedAuthorities());

        if (userEntity.isAccountLocked()) {
            builder.accountLocked(true);
        }
        if (userEntity.isAccountExpired()) {
            builder.accountExpired(true);
        }
        if (userEntity.isCredentialsExpired()) {
            builder.credentialsExpired(true);
        }
        if (userEntity.isDisabled()) {
            builder.disabled(true);
        }

        return builder.build();
    }
}
