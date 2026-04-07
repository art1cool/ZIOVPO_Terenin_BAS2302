package repository;

import entity.SignatureAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SignatureAuditRepository extends JpaRepository<SignatureAuditEntity, Long> {
    List<SignatureAuditEntity> findBySignatureIdOrderByChangedAtDesc(UUID signatureId);
}