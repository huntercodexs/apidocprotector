package com.apidocprotector.config.oauth2.repository;

import com.apidocprotector.config.oauth2.model.OperatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperatorRepository extends JpaRepository<OperatorEntity, Long> {
    OperatorEntity findByUsername(String username);
}
