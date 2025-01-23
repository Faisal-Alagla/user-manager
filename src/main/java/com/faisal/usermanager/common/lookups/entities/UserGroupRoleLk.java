package com.faisal.usermanager.common.lookups.entities;

import com.faisal.usermanager.utils.baseclasses.BaseLookupEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_group_role_lk")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class UserGroupRoleLk extends BaseLookupEntity {

}
