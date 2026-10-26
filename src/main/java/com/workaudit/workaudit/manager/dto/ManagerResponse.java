package com.workaudit.workaudit.manager.dto;

public class ManagerResponse {

    private Long id;
    private String name;

    public ManagerResponse() {
    }

    public ManagerResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}