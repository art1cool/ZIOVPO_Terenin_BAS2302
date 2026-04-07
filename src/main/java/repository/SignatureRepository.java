package repository;

import entity.SignatureEntity;
import enums.SignatureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SignatureRepository extends JpaRepository<SignatureEntity, UUID> {
    List<SignatureEntity> findByStatus(SignatureStatus status);
    List<SignatureEntity> findByUpdatedAtAfter(Instant since);
}