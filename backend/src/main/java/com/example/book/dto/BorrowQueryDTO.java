package com.example.book.dto;
import lombok.Data;
@Data
public class BorrowQueryDTO {
    private Long userId; private Long bookId; private Integer status;
    private Integer pageNum = 1; private Integer pageSize = 10;
}
