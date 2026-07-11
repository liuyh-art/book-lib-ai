package com.example.book.service.impl;

import com.example.book.util.BookAgentTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class BookAiAgentService {
    private final ChatClient chatClient;
    private final BookAgentTool bookAgentTool;
    private final BookRagService ragService;

    public BookAiAgentService(ChatClient.Builder builder, BookAgentTool bookAgentTool, BookRagService ragService) {
        this.chatClient = builder.build();
        this.bookAgentTool = bookAgentTool;
        this.ragService = ragService;
    }

    public String chat(String userName, String question){

        // 1. 获取图书借阅规则
        String rule = ragService.getBookRule();

        // 2. 智能体提示词（核心ReAct逻辑）
        String prompt = (" 你是图书管理智能助手，严格按照规则和真实数据回答。" +
                " 规则：{rule} " +
                "用户：{userName} " +
                "用户问题：{question} " +
                "你可以调用工具查询用户借阅数据、图书数据。 禁止编造数据，没有数据就如实告知。 ")
                .replace("{rule}", rule)
                .replace("{userId}", userName.toString())
                .replace("{question}", question);

        // 3. 绑定工具，AI自动思考、自动调用SQL、自动分析
        return chatClient.prompt(prompt)
                .tools(bookAgentTool)
                .call()
                .content();
    }
}
