package com.example.book.vo;
import lombok.Data;
@Data
public class BookVO { private Long id; private String bookName; private String author; private String publisher; private String isbn; private Long categoryId; private String categoryName; private Integer stock; private Integer totalStock; private String description; private String cover; private Integer status; private String createTime; private String updateTime; }
