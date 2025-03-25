package com.example.atp_back.user;

import com.example.atp_back.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // TODO: N+1 문제 해결할 것: SELECT ... FROM user JOIN  WHERE user.email = ...
    @Query("select u from User u " +
            "where u.email = :email") // fetch 조인은 OneToMany 관계들에 대해 한 번만 쓸 수 있다.
    Optional<User> findByEmail(String email);
}
