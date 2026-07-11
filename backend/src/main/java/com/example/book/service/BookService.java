package com.example.book.service;
import com.example.book.dto.BookDTO;
import com.example.book.vo.BookVO;
import com.example.book.vo.PageVO;
import java.util.List;
public interface BookService {
    PageVO<BookVO> listBooks(int pageNum, int pageSize, String bookName, String author, Long categoryId, Integer status);
    void addBook(BookDTO dto);
    void updateBook(BookDTO dto);
    void deleteBook(Long id);
    BookVO getById(Long id);
    List<BookVO> listAll();
}
