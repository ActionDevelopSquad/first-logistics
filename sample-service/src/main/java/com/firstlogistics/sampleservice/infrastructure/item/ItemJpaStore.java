package com.firstlogistics.sampleservice.infrastructure.item;

import com.firstlogistics.sampleservice.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemJpaStore extends JpaRepository<Item, Long> {
}
