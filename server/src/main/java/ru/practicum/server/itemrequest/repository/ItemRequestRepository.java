package ru.practicum.server.itemrequest.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.itemrequest.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorIdOrderByCreatedAsc(Long userId);

    List<ItemRequest> findAllByRequestorIdIsNot(Long userId, PageRequest pageRequest);
}
