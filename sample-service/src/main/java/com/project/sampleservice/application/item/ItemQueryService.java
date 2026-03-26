package com.project.sampleservice.application.item;

import com.project.sampleservice.infrastructure.entity.Item;
import com.project.sampleservice.domain.ItemRepository;
import com.project.sampleservice.domain.exception.ItemErrorCode;
import com.project.sampleservice.domain.exception.ItemException;
import com.project.sampleservice.presentation.item.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemQueryService {

    private final ItemRepository itemRepository;

    public ItemResponse getItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemException(ItemErrorCode.ITEM_NOT_FOUND));
        return ItemResponse.fromEntity(item);
    }

    public List<ItemResponse> getItems() {
        return itemRepository.findAll().stream()
                .map(ItemResponse::fromEntity)
                .toList();
    }
}
