package com.firstlogistics.sampleservice.application.item;

import com.firstlogistics.sampleservice.domain.item.Item;
import com.firstlogistics.sampleservice.domain.item.ItemRepository;
import com.firstlogistics.sampleservice.domain.item.exception.ItemErrorCode;
import com.firstlogistics.sampleservice.domain.item.exception.ItemException;
import com.firstlogistics.sampleservice.presentation.item.dto.ItemResponse;
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
