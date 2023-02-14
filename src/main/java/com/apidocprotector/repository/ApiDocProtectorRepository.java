package com.apidocprotector.repository;

import com.apidocprotector.model.ApiDocProtectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.NonUniqueResultException;

@Repository
public interface ApiDocProtectorRepository extends JpaRepository<ApiDocProtectorEntity, Long> {
    ApiDocProtectorEntity findByEmail(String email) throws NonUniqueResultException;
    ApiDocProtectorEntity findByToken(String token) throws NonUniqueResultException;
    ApiDocProtectorEntity findByUsernameOrEmail(String username, String email) throws NonUniqueResultException;
    ApiDocProtectorEntity findByTokenAndActive(String token, String active);
    ApiDocProtectorEntity findByUsernameAndPasswordAndTokenAndActive(String username, String password, String token, String active);
    ApiDocProtectorEntity findBySessionKeyAndActive(String key, String active);
    ApiDocProtectorEntity findByTokenAndRoleAndActive(String token, String role, String active);
}
