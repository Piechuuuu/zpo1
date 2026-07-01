package com.parcellocker.controller;

import com.parcellocker.dto.ParcelRequest;
import com.parcellocker.dto.ParcelResponse;
import com.parcellocker.security.CustomUserDetails;
import com.parcellocker.service.ParcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcels")
@RequiredArgsConstructor
@Tag(name = "Parcels", description = "User parcel endpoints")
public class ParcelController {

    private final ParcelService parcelService;

    @PostMapping
    @Operation(summary = "Send a parcel", description = "Creates a parcel and assigns it to the smallest available locker")
    public ResponseEntity<ParcelResponse> sendParcel(
            @Valid @RequestBody ParcelRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                parcelService.sendParcel(request, userDetails.getAppUser())
        );
    }

    @GetMapping
    @Operation(summary = "Get my parcels", description = "Returns parcels for the authenticated user only")
    public ResponseEntity<List<ParcelResponse>> getMyParcels(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                parcelService.getParcelsForUser(userDetails.getAppUser())
        );
    }
}
