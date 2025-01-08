package com.tylerpants.mokrynos_admin_bot.data.service;

import com.tylerpants.mokrynos_admin_bot.data.model.Symptom;
import com.tylerpants.mokrynos_admin_bot.data.repo.SymptomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SymptomService {
    private final SymptomRepository symptomRepository;

    @Autowired
    public SymptomService(SymptomRepository symptomRepository) {
        this.symptomRepository = symptomRepository;
    }

    public void save(Symptom symptom) {
        symptomRepository.save(symptom);
    }

    public List<Symptom> findAll() {
        return symptomRepository.findAll();
    }

    public Optional<Symptom> findById(Integer id) {
        return symptomRepository.findById(id);
    }

    public List<Symptom> findPageable(int p) {
        return symptomRepository.findAll(PageRequest.of(p, 3)).getContent();
    }

    public int count() {
        return (int) symptomRepository.count();
    }

}
