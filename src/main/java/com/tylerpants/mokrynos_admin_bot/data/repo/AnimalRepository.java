package com.tylerpants.mokrynos_admin_bot.data.repo;

import com.tylerpants.mokrynos_admin_bot.data.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {
}
