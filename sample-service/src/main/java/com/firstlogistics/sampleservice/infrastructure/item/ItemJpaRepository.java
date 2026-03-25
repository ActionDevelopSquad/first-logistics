package com.firstlogistics.sampleservice.infrastructure.item;

import com.firstlogistics.sampleservice.domain.item.Item;
import com.firstlogistics.sampleservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemJpaRepository implements ItemRepository {

    private final ItemJpaStore itemJpaStore;

    @Override
    public Item save(Item item) {
        return itemJpaStore.save(item);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemJpaStore.findById(id);
    }

    @Override
    public List<Item> findAll() {
        return itemJpaStore.findAll();
    }

    @Override
    public void delete(Item item) {
        itemJpaStore.delete(item);
    }
}
