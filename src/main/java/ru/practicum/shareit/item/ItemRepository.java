package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndOwnerId(Long itemId, Long userId);

    List<Item> findItemsByOwnerId(long id);

    @Query("SELECT i FROM Item i WHERE i.owner.id = ?1 AND i.available = true " +
            "AND (LOWER(i.name) LIKE ?2 OR LOWER(i.description) LIKE ?2)")
    List<Item> searchItems(long userId, String queryLower);
}
