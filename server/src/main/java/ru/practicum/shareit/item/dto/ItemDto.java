package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
    private Long ownerId;

    public ItemDto(long id, Long owner, String name) {
        this.id = id;
        this.ownerId = owner;
        this.name = name;
    }

    public ItemDto() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
