package com.parcellocker.service;

import com.parcellocker.entity.Locker;
import com.parcellocker.enums.LockerSize;
import com.parcellocker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LockerAllocationService {

    private final LockerRepository lockerRepository;

    /**
     * Finds the smallest available locker that fits the given parcel dimensions.
     * Tries fitting sizes from smallest to largest — if all lockers of the smallest
     * fitting size are occupied, cascades to the next larger size.
     *
     * Fully functional: uses streams, predicates from enum, no explicit loops.
     */
    public Optional<Locker> allocateLocker(int width, int height, int depth) {
        int[] sorted = sortDimensions(width, height, depth);

        return findAllFittingSizes(sorted)
                .map(lockerRepository::findFirstByLockerSizeAndOccupiedFalse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
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
     * Returns all LockerSizes that fit the parcel, sorted smallest to largest.
     * Used to cascade: if smallest fitting size has no free lockers, try next.
     */
    public Stream<LockerSize> findAllFittingSizes(int[] sortedDimensions) {
        return LockerSize.sortedByVolume()
                .filter(size -> size.fitsPredicate().test(sortedDimensions));
    }
}
