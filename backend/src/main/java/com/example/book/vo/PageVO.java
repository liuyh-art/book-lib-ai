package com.example.book.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
@Data @AllArgsConstructor
public class PageVO<T> { private List<T> list; private long total; private int pageNum; private int pageSize; private int pages; }
