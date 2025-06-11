package com.faisal.usermanager.user;

import com.faisal.usermanager.utils.baseclasses.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

}
