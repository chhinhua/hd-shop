package com.hdshop.repository;

import com.hdshop.entity.User;
import com.hdshop.utils.EnumOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByEmail(String email);

    boolean existsUserByEmail(String email);

    boolean existsUserByUsername(String username);

    boolean existsUserByPhoneNumber(String username);

    @Query(value = "SELECT email FROM users WHERE username = :username", nativeQuery = true)
    String getEmailByUsername(@Param("username") String username);

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Page<User> findAllByRoleName(@Param("roleName") String roleName, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE YEAR(u.createdDate) = :year AND MONTH(u.createdDate) = :month")
    Long getMonthlyUserCounts(@Param("month") int month, @Param("year") int year);
}
