package com.example.market.model.entity;


import com.example.market.model.enumclass.ProductStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@ToString(exclude = {"bookmarkList"})
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private int price;

    private LocalDateTime uploadDate;

    private LocalDateTime reUpDate;

    private boolean isDeleted;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Bookmark> bookmarkList;

    @ManyToOne
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Image> imageList;

}
