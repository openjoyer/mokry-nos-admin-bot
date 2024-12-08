package com.tylerpants.mokrynos_admin_bot.data.repo;

import com.tylerpants.mokrynos_admin_bot.data.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

}