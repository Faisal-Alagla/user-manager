package com.faisal.usermanager.group;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface GroupJpaRepository extends JpaRepository<Group, UUID> {

    Optional<Group> findByIdAndIsActiveTrue(UUID taskId);

    @Transactional
    @Modifying
    @Query("UPDATE Group g SET g.isActive = false WHERE g.id = :groupId")
    void deactivateGroup(UUID groupId);

}
