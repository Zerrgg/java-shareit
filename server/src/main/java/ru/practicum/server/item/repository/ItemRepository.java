package ru.practicum.server.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.item.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i " +
            "from Item as i " +
            "where i.available = true and " +
            "(lower(i.name) like lower(concat('%', ?1, '%') ) or " +
            "lower(i.description) like lower(concat('%', ?1, '%') ))")
    List<Item> search(String text, PageRequest pageRequest);

    List<Item> findAllByOwnerIdOrderByIdAsc(Long userId, PageRequest pageRequest);

    List<Item> findAllByRequestId(Long requestId);
}