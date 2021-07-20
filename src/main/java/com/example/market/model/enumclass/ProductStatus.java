package com.example.market.model.enumclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {
    sale(0,"판매중"),
    reserved(1,"예약"),
    soldOut(2,"판매완료")
    ;
    private int id;
    private String name;
}
