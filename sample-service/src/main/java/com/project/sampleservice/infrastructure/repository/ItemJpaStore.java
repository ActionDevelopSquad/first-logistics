package com.project.sampleservice.infrastructure.repository;

import com.project.sampleservice.infrastructure.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemJpaStore extends JpaRepository<Item, Long> {
}
