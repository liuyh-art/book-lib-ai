package com.example.book.controller;
import com.example.book.common.Result;
import com.example.book.service.BorrowRecordService;
import com.example.book.vo.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/dashboard")
public class DashboardController {
    @Autowired private BorrowRecordService borrowRecordService;
    @GetMapping("/stats") public Result<DashboardVO> getStats() { return Result.success(borrowRecordService.getDashboard()); }
}
