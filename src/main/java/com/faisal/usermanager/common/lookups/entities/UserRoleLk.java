package com.faisal.usermanager.common.lookups.entities;

import com.faisal.usermanager.utils.Interfaces.BaseLookupStringIdResponseInterface;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_role_lk")
@Getter
@Setter
public class UserRoleLk implements BaseLookupStringIdResponseInterface {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name")
    private String name;

}