package com.faisal.usermanager.common.lookups;

import com.faisal.usermanager.utils.annotations.NoActivityLogging;
import com.faisal.usermanager.utils.constants.BaseRoutingConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "Lookup Operations",
        description = "Endpoints for Lookup operations"
)
@RestController
@RequestMapping(BaseRoutingConstants.API + BaseRoutingConstants.V1 + BaseRoutingConstants.LOOKUP)
@RequiredArgsConstructor
@NoActivityLogging
public class LookupController {

    private final LookupService lookupService;

    @Operation(
            summary = "Group Visibility Lookup",
            description = "Get all lookups for group visibility types"
    )
    @GetMapping("/group-visibility")
    ResponseEntity<List<LookupResponseDto>> getGroupVisibilityLookup() {

        List<LookupResponseDto> groupVisibility = lookupService.getLookup(LookupType.GROUP_VISIBILITY).toList();
        return new ResponseEntity<>(groupVisibility, HttpStatus.OK);
    }

}
