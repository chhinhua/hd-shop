package com.duck.repository;

import com.duck.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Page<User> findAllByRoleName(@Param("roleName") String roleName, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE YEAR(u.createdDate) = :year AND MONTH(u.createdDate) = :month AND u.isEnabled=true")
    Long getMonthlyUserCounts(@Param("month") int month, @Param("year") int year);

    // TODO vấn đề lấy tất cả lại được cả admin nhưng khi seach thì không ra"
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.username != 'admin'" +
            "AND u.isEnabled = true AND u.isLocked = false " +
            "AND (:key IS NOT NULL AND (LOWER(u.name) LIKE %:key% " +
            "OR LOWER(u.username) LIKE %:key% " +
            "OR LOWER(u.phoneNumber) LIKE %:key%)) " +
            "OR (:key IS NULL) " +
            "ORDER BY " +
            "CASE WHEN 'id:asc' IN :sortCriteria THEN u.id END ASC, " +
            "CASE WHEN 'id:desc' IN :sortCriteria THEN u.id END DESC, " +
            "CASE WHEN 'username:asc' IN :sortCriteria THEN u.username END ASC, " +
            "CASE WHEN 'username:desc' IN :sortCriteria THEN u.username END DESC")
    Page<User> filter(
            @Param("roleName") String roleName,
            @Param("key") String key,
            @Param("sortCriteria") List<String> sortCriteria,
            Pageable pageable);
}
