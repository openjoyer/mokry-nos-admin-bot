package com.tylerpants.mokrynos_admin_bot.data.service;

import com.tylerpants.mokrynos_admin_bot.data.model.Animal;
import com.tylerpants.mokrynos_admin_bot.data.repo.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalService {
    private final AnimalRepository animalRepository;

    @Autowired
    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public void save(Animal animal) {
        animalRepository.save(animal);
    }

    public List<Animal> findAll() {
        return animalRepository.findAll();
    }

    public List<Animal> findPageable(int p) {
        return animalRepository.findAll(PageRequest.of(p, 3)).getContent();
    }

    public int count() {
        return (int) animalRepository.count();
    }

}
