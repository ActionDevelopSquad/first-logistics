package com.project.sampleservice.infrastructure.item;

import com.project.sampleservice.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemJpaStore extends JpaRepository<Item, Long> {
}
