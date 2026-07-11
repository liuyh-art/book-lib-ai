package com.example.book.controller;
import com.example.book.common.JwtInterceptor;
import com.example.book.common.Result;
import com.example.book.dto.BorrowDTO;
import com.example.book.service.BorrowRecordService;
import com.example.book.vo.BorrowRecordVO;
import com.example.book.vo.PageVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/borrow")
public class BorrowController {
    @Autowired private BorrowRecordService borrowRecordService;
    @PostMapping("/borrow") public Result<Void> borrowBook(@Valid @RequestBody BorrowDTO dto) {
        JwtInterceptor.UserContext user = JwtInterceptor.getCurrentUser();
        borrowRecordService.borrowBook(user.getUserId(), dto.getBookId());
        return Result.success("借书成功", null);
    }
    @PostMapping("/return") public Result<Void> returnBook(@Valid @RequestBody BorrowDTO dto) {
        JwtInterceptor.UserContext user = JwtInterceptor.getCurrentUser();
        borrowRecordService.returnBook(user.getUserId(), dto.getBookId());
        return Result.success("还书成功", null);
    }
    @GetMapping("/page")
    public Result<PageVO<BorrowRecordVO>> listRecords(
            @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long userId, @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer status) {
        return Result.success(borrowRecordService.listRecords(pageNum, pageSize, userId, bookId, status));
    }
    @GetMapping("/my")
    public Result<PageVO<BorrowRecordVO>> myRecords(
            @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        JwtInterceptor.UserContext user = JwtInterceptor.getCurrentUser();
        return Result.success(borrowRecordService.listRecords(pageNum, pageSize, user.getUserId(), null, status));
    }
}
