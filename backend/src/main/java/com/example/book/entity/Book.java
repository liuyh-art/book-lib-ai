package com.example.book.entity;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class Book {
    private Long id; private String bookName; private String author; private String publisher;
    private String isbn; private Long categoryId; private Integer stock; private Integer totalStock;
    private String description; private String cover; private Integer status;
    private Integer isDeleted; private LocalDateTime createTime; private LocalDateTime updateTime;
}
