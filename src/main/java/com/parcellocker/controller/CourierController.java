package com.parcellocker.controller;

import com.parcellocker.dto.ParcelResponse;
import com.parcellocker.enums.ParcelStatus;
import com.parcellocker.service.ParcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courier")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COURIER')")
@Tag(name = "Courier", description = "Courier-only endpoints")
public class CourierController {

    private final ParcelService parcelService;

    @GetMapping("/parcels")
    @Operation(summary = "Get all parcels", description = "Courier sees all parcels from all users")
    public ResponseEntity<List<ParcelResponse>> getAllParcels() {
        return ResponseEntity.ok(parcelService.getAllParcels());
    }

    @PutMapping("/parcels/{id}/pickup")
    @Operation(summary = "Confirm parcel pickup", description = "Courier confirms picking up a parcel from locker")
    public ResponseEntity<ParcelResponse> pickupParcel(@PathVariable Long id) {
        return ResponseEntity.ok(
                parcelService.updateParcelStatus(id, ParcelStatus.PICKED_UP)
        );
    }

    @PutMapping("/parcels/{id}/deliver")
    @Operation(summary = "Confirm parcel delivery", description = "Courier confirms delivering a parcel to locker")
    public ResponseEntity<ParcelResponse> deliverParcel(@PathVariable Long id) {
        return ResponseEntity.ok(
                parcelService.updateParcelStatus(id, ParcelStatus.IN_LOCKER)
        );
    }
}
