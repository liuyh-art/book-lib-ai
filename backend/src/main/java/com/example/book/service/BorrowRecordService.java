package com.example.book.service;
import com.example.book.vo.BorrowRecordVO;
import com.example.book.vo.DashboardVO;
import com.example.book.vo.PageVO;
public interface BorrowRecordService {
    void borrowBook(Long userId, Long bookId);
    void returnBook(Long userId, Long bookId);
    PageVO<BorrowRecordVO> listRecords(int pageNum, int pageSize, Long userId, Long bookId, Integer status);
    DashboardVO getDashboard();
}
