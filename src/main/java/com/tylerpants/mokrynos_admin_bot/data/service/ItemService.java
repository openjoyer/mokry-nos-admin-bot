package com.tylerpants.mokrynos_admin_bot.data.service;

import com.tylerpants.mokrynos_admin_bot.data.model.Item;
import com.tylerpants.mokrynos_admin_bot.data.model.Symptom;
import com.tylerpants.mokrynos_admin_bot.data.repo.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final SymptomService symptomService;

    @Autowired
    public ItemService(ItemRepository itemRepository, SymptomService symptomService) {
        this.itemRepository = itemRepository;
        this.symptomService = symptomService;
    }

    public void save(Item item) {
        itemRepository.save(item);
    }

    public Item decodeItemObject(String data) {
        String[] arr = data.split(";");

        String name = arr[0];
        String description = arr[1];
        String animalIdArr = arr[2].trim().replaceAll(" ", ",");

        String symptomStr = arr[3].trim();
        Symptom symptom = symptomService.findById(Integer.parseInt(symptomStr)).orElseThrow();

        String link = arr[4];

        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAnimalsArr(animalIdArr);
        item.setSymptom(symptom);
        item.setCatalogLink(link);

        return item;
    }
}
