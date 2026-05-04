package com.hms.user.UserMS.repository;

import com.hms.user.UserMS.dto.MonthlyRoleCountDTO;
import com.hms.user.UserMS.dto.Roles;
import com.hms.user.UserMS.dto.UserStatus;
import com.hms.user.UserMS.entity.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public List<MonthlyRoleCountDTO> countRegistrationsByRoleGroupedByMonth(Roles role) {
        return getEntityManager().createQuery(
                "SELECT new com.hms.user.UserMS.dto.MonthlyRoleCountDTO(" +
                "CAST(FUNCTION('MONTHNAME', a.createdAt) AS string), COUNT(a)) " +
                "FROM User a " +
                "WHERE a.role = :role AND YEAR(a.createdAt) = YEAR(CURRENT_DATE) " +
                "GROUP BY FUNCTION('MONTH', a.createdAt), CAST(FUNCTION('MONTHNAME', a.createdAt) AS string) " +
                "ORDER BY FUNCTION('MONTH', a.createdAt)", MonthlyRoleCountDTO.class)
                .setParameter("role", role)
                .getResultList();
    }

    public List<User> findByRoleAndStatus(Roles role, UserStatus status) {
        return list("role = ?1 and status = ?2", role, status);
    }
}

