package com.example.book.dto;
import lombok.Data;
@Data
public class BookQueryDTO {
    private String bookName; private String author; private Long categoryId; private Integer status;
    private Integer pageNum = 1; private Integer pageSize = 10;
}
