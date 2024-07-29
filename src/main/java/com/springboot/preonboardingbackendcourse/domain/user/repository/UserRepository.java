package com.springboot.preonboardingbackendcourse.domain.user.repository;

import com.springboot.preonboardingbackendcourse.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

}
