package com.faisal.usermanager.common.lookups;

import com.faisal.usermanager.common.lookups.entities.GroupVisibilityLk;
import com.faisal.usermanager.common.lookups.entities.UserGroupRoleLk;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class LookupRepository {

    @PersistenceContext
    private EntityManager entityManager;

    List<GroupVisibilityLk> getGroupVisibilityLookup() {
        TypedQuery<GroupVisibilityLk> query = entityManager.createQuery(
                "SELECT gv FROM GroupVisibilityLk as gv",
                GroupVisibilityLk.class
        );
        return query.getResultList();
    }

    List<UserGroupRoleLk> getUserGroupRoleLookup() {
        TypedQuery<UserGroupRoleLk> query = entityManager.createQuery(
                "SELECT ugr FROM UserGroupRoleLk as ugr",
                UserGroupRoleLk.class
        );
        return query.getResultList();
    }

}
