package com.example.book.entity;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class BookCategory {
    private Long id; private String categoryName; private Integer sort;
    private Integer status; private Integer isDeleted; private LocalDateTime createTime; private LocalDateTime updateTime;
}
