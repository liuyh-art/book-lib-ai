package com.example.book.vo;
import lombok.Data;
@Data
public class BorrowRecordVO { private Long id; private Long userId; private String userName; private String realName; private Long bookId; private String bookName; private String author; private String isbn; private String borrowTime; private String returnTime; private Integer status; private String createTime; }
