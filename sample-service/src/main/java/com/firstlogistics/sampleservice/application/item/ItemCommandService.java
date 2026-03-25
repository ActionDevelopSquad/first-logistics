package com.project.sampleservice.application.item;

import com.project.sampleservice.application.item.command.CreateItemCommand;
import com.project.sampleservice.application.item.command.UpdateItemCommand;
import com.project.sampleservice.domain.item.Item;
import com.project.sampleservice.domain.item.ItemRepository;
import com.project.sampleservice.domain.item.exception.ItemErrorCode;
import com.project.sampleservice.domain.item.exception.ItemException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemCommandService {

    private final ItemRepository itemRepository;

    public Long createItem(CreateItemCommand command) {
        Item item = Item.create(command.name(), command.description());
        return itemRepository.save(item).getId();
    }

    public Long updateItem(UpdateItemCommand command) {
        Item item = itemRepository.findById(command.id())
                .orElseThrow(() -> new ItemException(ItemErrorCode.ITEM_NOT_FOUND));
        item.update(command.name(), command.description());
        return item.getId();
    }

    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemException(ItemErrorCode.ITEM_NOT_FOUND));
        itemRepository.delete(item);
    }
}
