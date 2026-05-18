package com.glee.xjpa.proxy;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "users")
class TestUser implements Serializable {

    public static final String ID = "id";
    public static final String FIRST_NAME = "first_name";

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}