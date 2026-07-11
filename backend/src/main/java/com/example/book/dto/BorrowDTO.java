package com.example.book.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class BorrowDTO {
    @NotNull(message = "图书ID不能为空") private Long bookId;
    private Long userId;
}
