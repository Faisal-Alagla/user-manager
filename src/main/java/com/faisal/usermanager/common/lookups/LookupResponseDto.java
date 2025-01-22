package com.faisal.usermanager.common.lookups;

import com.faisal.usermanager.utils.Interfaces.BaseLookupResponseInterface;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LookupResponseDto {

    private Integer id;

    private String name;

    static LookupResponseDto fromEntity(BaseLookupResponseInterface fileStatus) {
        return new LookupResponseDto(
                fileStatus.getId(),
                fileStatus.getName()
        );
    }
}
