package com.example.book.controller;

import com.example.book.common.JwtInterceptor;
import com.example.book.common.Result;
import com.example.book.service.impl.BookAiAgentService;
import com.example.book.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class BookAiController {
    @Autowired
    private BookAiAgentService bookAiAgentService;

    @GetMapping("/chat")
    public Result aiChat( @RequestParam(name = "question") String question){
        String username = JwtInterceptor.getCurrentUser().getUsername();
        String chat = bookAiAgentService.chat(username, question);
        return Result.success("操作成功",chat);
    }
}
