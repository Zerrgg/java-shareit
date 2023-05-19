package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemMapper {
    public ItemDto intoDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public Item intoModel(ItemDto itemDto, Long ownerId) {
        return new Item(null, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), ownerId);
    }

    public List<ItemDto> intoDtoList(List<Item> userItems) {
        if (userItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<ItemDto> result = new ArrayList<>();
        for (Item item : userItems) {
            ItemDto itemDto = intoDto(item);
            result.add(itemDto);
        }
        return result;
    }
}