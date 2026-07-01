package com.parcellocker.entity;

import com.parcellocker.enums.LockerSize;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lockers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Locker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LockerSize lockerSize;

    @Column(nullable = false)
    private Boolean occupied;
}
