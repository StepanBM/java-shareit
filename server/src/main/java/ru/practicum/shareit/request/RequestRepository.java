package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByOrderByCreatedDesc();

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(long userId);

    List<ItemRequest> findAllByRequestor(User requestor);
}
