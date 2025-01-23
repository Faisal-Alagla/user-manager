package com.faisal.usermanager.group;

import com.faisal.usermanager.utils.baseclasses.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "group")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class Group extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "group_image_url")
    private String groupImageUrl;

    @Column(name = "visibility_id")
    private Integer visibilityId;

}
