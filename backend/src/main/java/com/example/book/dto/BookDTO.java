package com.example.book.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class BookDTO {
    private Long id;
    @NotBlank private String bookName; private String author; private String publisher; private String isbn;
    @NotNull private Long categoryId;
    @NotNull private Integer stock;
    @NotNull private Integer totalStock;
    private String description; private String cover; private Integer status;
}
