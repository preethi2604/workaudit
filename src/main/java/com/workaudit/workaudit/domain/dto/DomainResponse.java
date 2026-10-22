package com.workaudit.workaudit.domain.dto;

public class DomainResponse {

    private Long id;
    private String name;

    public DomainResponse() {
    }

    public DomainResponse(Long id, String name) {
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