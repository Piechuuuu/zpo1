package com.parcellocker.service;

import com.parcellocker.dto.ParcelRequest;
import com.parcellocker.dto.ParcelResponse;
import com.parcellocker.entity.AppUser;
import com.parcellocker.entity.Locker;
import com.parcellocker.entity.Parcel;
import com.parcellocker.enums.ParcelStatus;
import com.parcellocker.enums.Role;
import com.parcellocker.repository.ParcelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final LockerAllocationService lockerAllocationService;

    public List<ParcelResponse> getParcelsForUser(AppUser user) {
        return parcelRepository.findBySenderId(user.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ParcelResponse> getAllParcels() {
        return parcelRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ParcelResponse> resolveParcelsByRole(AppUser user) {
        return Role.ROLE_ADMIN.equals(user.getRole()) || Role.ROLE_COURIER.equals(user.getRole())
                ? getAllParcels()
                : getParcelsForUser(user);
    }

    @Transactional
    public ParcelResponse sendParcel(ParcelRequest request, AppUser sender) {
        Locker locker = lockerAllocationService
                .allocateLocker(request.getWidth(), request.getHeight(), request.getDepth())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No available locker fits parcel dimensions: "
                                + request.getWidth() + "x" + request.getHeight() + "x" + request.getDepth() + " cm"));

        locker.setOccupied(true);

        Parcel parcel = Parcel.builder()
                .sender(sender)
                .recipientName(request.getRecipientName())
                .width(request.getWidth())
                .height(request.getHeight())
                .depth(request.getDepth())
                .locker(locker)
                .status(ParcelStatus.IN_LOCKER)
                .build();

        return mapToResponse(parcelRepository.save(parcel));
    }

    @Transactional
    public ParcelResponse updateParcelStatus(Long parcelId, ParcelStatus newStatus) {
        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new IllegalArgumentException("Parcel not found with id: " + parcelId));

        parcel.setStatus(newStatus);

        // Free locker when parcel is picked up
        java.util.Optional.of(newStatus)
                .filter(ParcelStatus.PICKED_UP::equals)
                .ifPresent(s -> parcel.getLocker().setOccupied(false));

        return mapToResponse(parcelRepository.save(parcel));
    }

    private ParcelResponse mapToResponse(Parcel parcel) {
        return ParcelResponse.builder()
                .id(parcel.getId())
                .senderUsername(parcel.getSender().getUsername())
                .recipientName(parcel.getRecipientName())
                .width(parcel.getWidth())
                .height(parcel.getHeight())
                .depth(parcel.getDepth())
                .lockerSize(parcel.getLocker().getLockerSize().name())
                .lockerId(parcel.getLocker().getId())
                .status(parcel.getStatus().name())
                .build();
    }
}
