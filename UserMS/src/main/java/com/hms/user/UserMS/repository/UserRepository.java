package com.hms.user.UserMS.repository;

import com.hms.user.UserMS.dto.MonthlyRoleCountDTO;
import com.hms.user.UserMS.dto.Roles;
import com.hms.user.UserMS.dto.UserDTO;
import com.hms.user.UserMS.dto.UserStatus;
import com.hms.user.UserMS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT new com.hms.user.UserMS.dto.MonthlyRoleCountDTO(" +
            "CAST(FUNCTION('MONTHNAME', a.createdAt) AS string), COUNT(a)) " +
            "FROM User a " +
            "WHERE a.role = ?1 AND YEAR(a.createdAt) = YEAR(CURRENT_DATE) " +
            "GROUP BY FUNCTION('MONTH', a.createdAt), CAST(FUNCTION('MONTHNAME', a.createdAt) AS string) " +
            "ORDER BY FUNCTION('MONTH', a.createdAt)")
    List<MonthlyRoleCountDTO> countRegistrationsByRoleGroupedByMonth(Roles role);
    List<User> findByRoleAndStatus(Roles role, UserStatus status);

}
