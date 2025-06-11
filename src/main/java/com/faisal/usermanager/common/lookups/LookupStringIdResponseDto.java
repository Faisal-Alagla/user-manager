package com.faisal.usermanager.common.lookups;

import com.faisal.usermanager.utils.Interfaces.BaseLookupStringIdResponseInterface;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LookupStringIdResponseDto {

    private String id;

    private String name;

    static LookupStringIdResponseDto fromEntity(BaseLookupStringIdResponseInterface lookupStringId) {
        return new LookupStringIdResponseDto(
                lookupStringId.getId(),
                lookupStringId.getName()
        );
    }
}
