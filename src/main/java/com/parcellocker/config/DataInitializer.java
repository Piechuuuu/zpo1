package com.parcellocker.config;

import com.parcellocker.entity.AppUser;
import com.parcellocker.entity.Locker;
import com.parcellocker.enums.LockerSize;
import com.parcellocker.enums.Role;
import com.parcellocker.repository.AppUserRepository;
import com.parcellocker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final LockerRepository lockerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initUsers();
        initLockers();
    }

    private void initUsers() {
        List.of(
                AppUser.builder()
                        .username("user1")
                        .password(passwordEncoder.encode("user1"))
                        .role(Role.ROLE_USER)
                        .build(),
                AppUser.builder()
                        .username("user2")
                        .password(passwordEncoder.encode("user2"))
                        .role(Role.ROLE_USER)
                        .build(),
                AppUser.builder()
                        .username("courier")
                        .password(passwordEncoder.encode("courier"))
                        .role(Role.ROLE_COURIER)
                        .build()
        ).forEach(userRepository::save);
    }

    private void initLockers() {
        Stream.of(LockerSize.values())
                .flatMap(size -> IntStream.rangeClosed(1, 5)
                        .mapToObj(i -> Locker.builder()
                                .lockerSize(size)
                                .occupied(false)
                                .build()))
                .forEach(lockerRepository::save);
    }
}
