package com.tylerpants.mokrynos_admin_bot.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@RequiredArgsConstructor
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "animals_arr")
    private String animalsArr;

    @ManyToOne
    @JoinColumn(name = "symptoms")
    private Symptom symptom;

    @Column(name = "description")
    private String description;

    @Column(name = "catalog_link")
    private String catalogLink;

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", animalsArr='" + animalsArr + '\'' +
                ", symptom=" + symptom +
                ", description='" + description + '\'' +
                ", catalogLink='" + catalogLink;
    }
}