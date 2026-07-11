package com.example.book.controller;
import com.example.book.common.Result;
import com.example.book.dto.BookDTO;
import com.example.book.service.BookService;
import com.example.book.vo.BookVO;
import com.example.book.vo.PageVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/book")
public class BookController {
    @Autowired private BookService bookService;
    @GetMapping("/page")
    public Result<PageVO<BookVO>> listBooks(
            @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String bookName, @RequestParam(required = false) String author,
            @RequestParam(required = false) Long categoryId, @RequestParam(required = false) Integer status) {
        return Result.success(bookService.listBooks(pageNum, pageSize, bookName, author, categoryId, status));
    }
    @GetMapping("/list") public Result<List<BookVO>> listAll() { return Result.success(bookService.listAll()); }
    @GetMapping("/{id}") public Result<BookVO> getById(@PathVariable Long id) { return Result.success(bookService.getById(id)); }
    @PostMapping public Result<Void> addBook(@Valid @RequestBody BookDTO dto) { bookService.addBook(dto); return Result.success("新增成功", null); }
    @PutMapping public Result<Void> updateBook(@Valid @RequestBody BookDTO dto) { bookService.updateBook(dto); return Result.success("更新成功", null); }
    @DeleteMapping("/{id}") public Result<Void> deleteBook(@PathVariable Long id) { bookService.deleteBook(id); return Result.success("删除成功", null); }
}
