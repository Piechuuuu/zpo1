package com.parcellocker.entity;

import com.parcellocker.enums.ParcelStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parcels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private AppUser sender;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private Integer width;

    @Column(nullable = false)
    private Integer height;

    @Column(nullable = false)
    private Integer depth;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "locker_id")
    private Locker locker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatus status;
}
