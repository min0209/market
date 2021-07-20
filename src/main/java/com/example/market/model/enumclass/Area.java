package com.example.market.model.enumclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Area {

    Seoul(1,"서울"),
    Incheon(2,"인천"),
    Daegu(3,"대구"),
    Daejeon(4,"대전"),
    Gyeonggido(5,"경기도"),
    Chungcheongnamdo(6,"충청남도"),
    Jeollanamdo(7,"전라남도"),
    Gyeongsangnamdo(8,"경상남도"),
    Busan(9,"부산"),
    Gwangju(10,"광주"),
    Ulsan(11,"울산"),
    Gangwondo(12,"강원도"),
    Chungcheongbukdo(13,"충청북도"),
    Jeollabukdo(14,"전라북도"),
    Gyeongsangbukdo(15,"경상북도"),
    Jejudo(16,"제주도")
    ;

    private Integer id;

    private String name;

}
