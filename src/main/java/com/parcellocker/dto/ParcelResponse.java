package com.parcellocker.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelResponse {

    private Long id;
    private String senderUsername;
    private String recipientName;
    private Integer width;
    private Integer height;
    private Integer depth;
    private String lockerSize;
    private Long lockerId;
    private String status;
}
