package com.faisal.usermanager.usergroup;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupKey implements Serializable {

    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID groupId;

    @Column(name = "group_id", insertable = false, updatable = false)
    private UUID userId;

}
