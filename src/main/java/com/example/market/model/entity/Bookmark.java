package com.example.market.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bid;

    @ManyToOne
    private Product product;

    @ManyToOne
    private User user;
}
