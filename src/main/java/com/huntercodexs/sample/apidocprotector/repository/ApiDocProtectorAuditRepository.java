package com.huntercodexs.sample.apidocprotector.repository;

import com.huntercodexs.sample.apidocprotector.model.ApiDocProtectorAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiDocProtectorAuditRepository extends JpaRepository<ApiDocProtectorAuditEntity, Long> {
}
