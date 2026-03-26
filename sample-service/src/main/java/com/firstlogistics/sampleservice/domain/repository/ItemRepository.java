package com.firstlogistics.sampleservice.domain.repository;

import com.firstlogistics.sampleservice.infrastructure.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(Long id);

    List<Item> findAll();

    void delete(Item item);
}
