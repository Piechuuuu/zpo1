package com.parcellocker.repository;

import com.parcellocker.entity.Locker;
import com.parcellocker.enums.LockerSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {

    List<Locker> findByOccupiedFalse();

    Optional<Locker> findFirstByLockerSizeAndOccupiedFalse(LockerSize lockerSize);
}
