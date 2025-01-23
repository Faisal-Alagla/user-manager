package com.faisal.usermanager.usergroup;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {

    @EmbeddedId
    private UserGroupKey id;

    @Transient
    private UUID userId;

    @Transient
    private UUID groupId;

    @Column(name = "user_group_role_id")
    private Integer userGroupRoleId;

}
