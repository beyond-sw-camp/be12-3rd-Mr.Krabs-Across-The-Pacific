package com.example.atp_back.user;

import com.example.atp_back.user.model.UserTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserTierRepository extends JpaRepository<UserTier, Long> {
    @Query("select u from UserTier u " +
            "join fetch u.userList l " +
            "where u.grade = :grade")
    public Optional<UserTier> findByGrade(String grade);
}
