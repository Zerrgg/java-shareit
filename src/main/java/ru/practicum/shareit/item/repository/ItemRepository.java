package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i " +
            "from Item i " +
            "where lower(trim(i.name)) like lower(trim(concat('%', ?1, '%'))) " +
            "or lower(trim(i.description)) like lower(trim(concat('%', ?1, '%'))) " +
            "and i.available = true")
    List<Item> search(String text);
}