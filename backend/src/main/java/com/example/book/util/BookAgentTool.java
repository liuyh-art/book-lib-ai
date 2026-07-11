package com.example.book.util;

import com.example.book.entity.BorrowRecord;
import com.example.book.mapper.BorrowRecordMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookAgentTool {
    @Autowired
    private BorrowRecordMapper borrowMapper;

    // 工具1：查询用户当前借阅图书
    @Tool(description = "根据用户ID查询用户当前正在借阅的图书列表")
    public List<String> getBorrowBookByUserId(Long userId){
        return borrowMapper.countBorrowingByUserId(userId);
    }

    // 工具2：查询用户逾期图书
    @Tool(description = "根据用户ID查询用户逾期未还图书")
    public List<String> getOverdueBook(Long userId){
        return borrowMapper.selectOverdueBook(userId);
    }

    // 工具3：根据分类查询可借阅图书
    @Tool(description = "根据图书分类查询当前可借阅图书")
    public List<String> getBookByCategory(String category){
        return borrowMapper.selectCanBorrowBook(category);
    }

    // 工具4：查询用户剩余可借数量
    @Tool(description = "查询用户剩余可借阅图书数量")
    public Integer getRemainBorrowNum(Long userId){
        return borrowMapper.selectRemainBorrowNum(userId);
    }
}
