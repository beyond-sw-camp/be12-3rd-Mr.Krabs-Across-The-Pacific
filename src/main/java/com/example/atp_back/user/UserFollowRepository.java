package com.example.atp_back.user;

import com.example.atp_back.user.model.User;
import com.example.atp_back.user.model.follow.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    public List<UserFollow> findAllByFolloweeOrderByDate(User followee);
    public List<UserFollow> findAllByFollowerOrderByDate(User follower);
    public Optional<UserFollow> findByFolloweeAndFollower(User followee, User follower);
}
