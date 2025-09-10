package ru.practicum.shareit.item;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @OneToOne(fetch = FetchType.LAZY)
    private ItemRequest request;

}
