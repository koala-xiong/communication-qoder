package com.communication.dto;

import com.communication.entity.Category;

public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private Integer sortOrder;

    public CategoryDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public static CategoryDto fromEntity(Category c) {
        CategoryDto dto = new CategoryDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setDescription(c.getDescription());
        dto.setIcon(c.getIcon());
        dto.setSortOrder(c.getSortOrder());
        return dto;
    }
}
