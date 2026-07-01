package com.parcellocker;

import com.parcellocker.entity.Locker;
import com.parcellocker.enums.LockerSize;
import com.parcellocker.repository.LockerRepository;
import com.parcellocker.service.LockerAllocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LockerAllocationServiceTest {

    @Mock
    private LockerRepository lockerRepository;

    @InjectMocks
    private LockerAllocationService allocationService;

    private Locker smallLocker;
    private Locker mediumLocker;
    private Locker largeLocker;

    @BeforeEach
    void setUp() {
        smallLocker = Locker.builder().id(1L).lockerSize(LockerSize.SMALL).occupied(false).build();
        mediumLocker = Locker.builder().id(2L).lockerSize(LockerSize.MEDIUM).occupied(false).build();
        largeLocker = Locker.builder().id(3L).lockerSize(LockerSize.LARGE).occupied(false).build();
    }

    @Test
    void shouldAllocateSmallLockerForSmallParcel() {
        when(lockerRepository.findFirstByLockerSizeAndOccupiedFalse(LockerSize.SMALL))
                .thenReturn(Optional.of(smallLocker));

        Optional<Locker> result = allocationService.allocateLocker(10, 8, 5);

        assertTrue(result.isPresent());
        assertEquals(LockerSize.SMALL, result.get().getLockerSize());
        verify(lockerRepository).findFirstByLockerSizeAndOccupiedFalse(LockerSize.SMALL);
    }

    @Test
    void shouldAllocateSmallLockerForExactSmallDimensions() {
        when(lockerRepository.findFirstByLockerSizeAndOccupiedFalse(LockerSize.SMALL))
                .thenReturn(Optional.of(smallLocker));

        Optional<Locker> result = allocationService.allocateLocker(20, 15, 10);

        assertTrue(result.isPresent());
        assertEquals(LockerSize.SMALL, result.get().getLockerSize());
    }

    @Test
    void shouldAllocateMediumLockerWhenTooLargeForSmall() {
        when(lockerRepository.findFirstByLockerSizeAndOccupiedFalse(LockerSize.MEDIUM))
                .thenReturn(Optional.of(mediumLocker));

        Optional<Locker> result = allocationService.allocateLocker(25, 20, 15);

        assertTrue(result.isPresent());
        assertEquals(LockerSize.MEDIUM, result.get().getLockerSize());
        verify(lockerRepository, never()).findFirstByLockerSizeAndOccupiedFalse(LockerSize.SMALL);
    }

    @Test
    void shouldAllocateLargeLockerWhenTooLargeForMedium() {
        when(lockerRepository.findFirstByLockerSizeAndOccupiedFalse(LockerSize.LARGE))
                .thenReturn(Optional.of(largeLocker));

        Optional<Locker> result = allocationService.allocateLocker(50, 40, 30);

        assertTrue(result.isPresent());
        assertEquals(LockerSize.LARGE, result.get().getLockerSize());
    }

    @Test
    void shouldReturnEmptyWhenParcelTooLargeForAllLockers() {
        Optional<Locker> result = allocationService.allocateLocker(100, 100, 100);

        assertTrue(result.isEmpty());
        verifyNoInteractions(lockerRepository);
    }

    @Test
    void shouldReturnEmptyWhenNoSmallLockerAvailable() {
        when(lockerRepository.findFirstByLockerSizeAndOccupiedFalse(LockerSize.SMALL))
                .thenReturn(Optional.empty());

        Optional<Locker> result = allocationService.allocateLocker(10, 8, 5);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSortDimensionsDescendingForOptimalFitting() {
        int[] sorted = allocationService.sortDimensions(5, 20, 10);

        assertArrayEquals(new int[]{20, 10, 5}, sorted);
    }

    @Test
    void shouldHandleDimensionsInAnyOrder() {
        when(lockerRepository.findFirstByLockerSizeAndOccupiedFalse(LockerSize.SMALL))
                .thenReturn(Optional.of(smallLocker));

        // 10x15x20 should fit SMALL (after sorting: 20x15x10)
        Optional<Locker> result = allocationService.allocateLocker(10, 15, 20);

        assertTrue(result.isPresent());
        assertEquals(LockerSize.SMALL, result.get().getLockerSize());
    }

    @Test
    void shouldAllocateMediumWhenOneDimensionExceedsSmall() {
        when(lockerRepository.findFirstByLockerSizeAndOccupiedFalse(LockerSize.MEDIUM))
                .thenReturn(Optional.of(mediumLocker));

        // 21 exceeds SMALL max width of 20
        Optional<Locker> result = allocationService.allocateLocker(21, 10, 5);

        assertTrue(result.isPresent());
        assertEquals(LockerSize.MEDIUM, result.get().getLockerSize());
    }

    @Test
    void shouldFindSmallestFittingSizeForSmallDims() {
        Optional<LockerSize> size = allocationService.findSmallestFittingSize(new int[]{15, 10, 8});

        assertTrue(size.isPresent());
        assertEquals(LockerSize.SMALL, size.get());
    }

    @Test
    void shouldFindSmallestFittingSizeForMediumDims() {
        Optional<LockerSize> size = allocationService.findSmallestFittingSize(new int[]{30, 25, 15});

        assertTrue(size.isPresent());
        assertEquals(LockerSize.MEDIUM, size.get());
    }

    @Test
    void shouldFindSmallestFittingSizeForLargeDims() {
        Optional<LockerSize> size = allocationService.findSmallestFittingSize(new int[]{55, 40, 30});

        assertTrue(size.isPresent());
        assertEquals(LockerSize.LARGE, size.get());
    }

    @Test
    void shouldReturnEmptyForOversizedDims() {
        Optional<LockerSize> size = allocationService.findSmallestFittingSize(new int[]{100, 100, 100});

        assertTrue(size.isEmpty());
    }

    @Test
    void shouldAllocateSmallLockerForMinimalParcel() {
        when(lockerRepository.findFirstByLockerSizeAndOccupiedFalse(LockerSize.SMALL))
                .thenReturn(Optional.of(smallLocker));

        Optional<Locker> result = allocationService.allocateLocker(1, 1, 1);

        assertTrue(result.isPresent());
        assertEquals(LockerSize.SMALL, result.get().getLockerSize());
    }

    @Test
    void shouldAllocateExactMediumDimensions() {
        when(lockerRepository.findFirstByLockerSizeAndOccupiedFalse(LockerSize.MEDIUM))
                .thenReturn(Optional.of(mediumLocker));

        Optional<Locker> result = allocationService.allocateLocker(40, 30, 20);

        assertTrue(result.isPresent());
        assertEquals(LockerSize.MEDIUM, result.get().getLockerSize());
    }

    @Test
    void shouldAllocateExactLargeDimensions() {
        when(lockerRepository.findFirstByLockerSizeAndOccupiedFalse(LockerSize.LARGE))
                .thenReturn(Optional.of(largeLocker));

        Optional<Locker> result = allocationService.allocateLocker(60, 45, 35);

        assertTrue(result.isPresent());
        assertEquals(LockerSize.LARGE, result.get().getLockerSize());
    }
}
