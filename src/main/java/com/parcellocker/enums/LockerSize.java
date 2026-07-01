package com.parcellocker.enums;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum LockerSize {

    SMALL(20, 15, 10),
    MEDIUM(40, 30, 20),
    LARGE(60, 45, 35);

    private final int maxWidth;
    private final int maxHeight;
    private final int maxDepth;

    LockerSize(int maxWidth, int maxHeight, int maxDepth) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.maxDepth = maxDepth;
    }

    public int getMaxWidth() { return maxWidth; }
    public int getMaxHeight() { return maxHeight; }
    public int getMaxDepth() { return maxDepth; }

    public long volume() {
        return (long) maxWidth * maxHeight * maxDepth;
    }

    public Predicate<int[]> fitsPredicate() {
        return dims -> dims[0] <= maxWidth && dims[1] <= maxHeight && dims[2] <= maxDepth;
    }

    public static Stream<LockerSize> sortedByVolume() {
        return Stream.of(values()).sorted(Comparator.comparingLong(LockerSize::volume));
    }
}
