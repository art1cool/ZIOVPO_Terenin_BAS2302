package repository;

import entity.SignatureHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SignatureHistoryRepository extends JpaRepository<SignatureHistoryEntity, Long> {
    List<SignatureHistoryEntity> findBySignatureIdOrderByVersionCreatedAtDesc(UUID signatureId);
}
