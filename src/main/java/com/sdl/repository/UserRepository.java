package com.sdl.repository;

import com.sdl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(User.Role role);

    boolean existsByStatusAndRole(User.UserStatus status, User.Role role);

    List<User> findByRole(User.Role role);

    List<User> findByRoleAndActiveTrue(User.Role role);

    List<User> findByCreatedBy_Id(Long createdById);

    List<User> findByStatusAndRole(User.UserStatus status, User.Role role);

    boolean existsByIdAndActiveTrue(Long id);
}