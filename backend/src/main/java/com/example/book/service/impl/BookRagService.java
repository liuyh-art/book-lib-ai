package com.example.book.service.impl;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class BookRagService {

    public String getBookRule(){
        // 读取本地图书规则txt
        try {
            Path path = Paths.get("src/main/resources/book_rule.txt");
            String content = Files.readString(path, StandardCharsets.UTF_8);
            return content;
        }catch (Exception e){
            return "";
        }
    }
}
