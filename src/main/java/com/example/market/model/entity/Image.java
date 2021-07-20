package com.example.market.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Entity
@Builder
@ToString(exclude = {"product"})
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fid;

    @NotEmpty
    private String path;

    @ManyToOne
    @JsonIgnore
    private Product product;

}
