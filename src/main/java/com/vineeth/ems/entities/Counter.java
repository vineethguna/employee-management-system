package com.vineeth.ems.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Counter {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

    private int counter;
}
