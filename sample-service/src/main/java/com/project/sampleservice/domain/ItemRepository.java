package com.project.sampleservice.domain;

import com.project.sampleservice.infrastructure.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(Long id);

    List<Item> findAll();

    void delete(Item item);
}
