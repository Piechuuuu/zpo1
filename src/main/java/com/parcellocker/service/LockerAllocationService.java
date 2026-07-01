package com.parcellocker.service;

import com.parcellocker.entity.Locker;
import com.parcellocker.enums.LockerSize;
import com.parcellocker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LockerAllocationService {

    private final LockerRepository lockerRepository;

    /**
     * Finds the smallest available locker that fits the given parcel dimensions.
     * Dimensions are sorted descending to match against locker max dimensions
     * (width >= height >= depth) for optimal fitting.
     *
     * Fully functional: uses streams, predicates from enum, no explicit loops.
     */
    public Optional<Locker> allocateLocker(int width, int height, int depth) {
        int[] sorted = sortDimensions(width, height, depth);

        return findSmallestFittingSize(sorted)
                .flatMap(lockerRepository::findFirstByLockerSizeAndOccupiedFalse);
    }

    /**
     * Sorts dimensions descending so the largest dimension is matched
     * against the largest locker dimension.
     */
    public int[] sortDimensions(int width, int height, int depth) {
        int[] dims = {width, height, depth};
        Arrays.sort(dims);
        return new int[]{dims[2], dims[1], dims[0]};
    }

    /**
     * Finds the smallest LockerSize whose max dimensions fit the sorted parcel dims.
     * Uses LockerSize.sortedByVolume() stream and fitsPredicate() — no ifs.
     */
    public Optional<LockerSize> findSmallestFittingSize(int[] sortedDimensions) {
        return LockerSize.sortedByVolume()
                .filter(size -> size.fitsPredicate().test(sortedDimensions))
                .findFirst();
    }
}
