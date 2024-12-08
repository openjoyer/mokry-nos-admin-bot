package com.tylerpants.mokrynos_admin_bot.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "symptoms")

@RequiredArgsConstructor
@Getter
@Setter
public class Symptom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Override
    public String toString() {
        return this.name;
    }
}