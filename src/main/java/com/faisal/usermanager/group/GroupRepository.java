package com.faisal.usermanager.group;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GroupRepository {
    //Delegate to GroupJpaRepository for straightforward CRUD operations and generated queries
    private final GroupJpaRepository groupJpaRepository;

    //Use EntityManager for advanced or custom queries that cannot be handled by JpaRepository
    @PersistenceContext
    private EntityManager entityManager;

    //region GroupJpaRepository methods
    public Group save(Group group) {
        return groupJpaRepository.save(group);
    }

    public Optional<Group> findByIdAndIsActiveTrue(UUID groupId) {
        return groupJpaRepository.findByIdAndIsActiveTrue(groupId);
    }
    //endregion

    //region EntityManager methods

    //endregion

}
