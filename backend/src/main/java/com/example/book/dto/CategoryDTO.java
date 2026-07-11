package com.example.book.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class CategoryDTO {
    private Long id;
    @NotBlank(message = "分类名称不能为空") private String categoryName;
    private Integer sort; private Integer status;
}
