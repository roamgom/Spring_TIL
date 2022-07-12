package jpabook.jpashop.service;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateItemDto {

    private Long id;
    private String name;
    private Integer price;
    private Integer stockQuantity;
}
