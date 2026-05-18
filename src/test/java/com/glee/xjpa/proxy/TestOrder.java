package com.glee.xjpa.proxy;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "orders")
class TestOrder implements Serializable {

    public static final String ID = "id";
    public static final String USER_ID = "user_id";

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}