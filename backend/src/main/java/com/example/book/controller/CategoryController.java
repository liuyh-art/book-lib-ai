package com.example.book.controller;
import com.example.book.common.Result;
import com.example.book.dto.CategoryDTO;
import com.example.book.service.BookCategoryService;
import com.example.book.vo.CategoryVO;
import com.example.book.vo.PageVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/category")
public class CategoryController {
    @Autowired private BookCategoryService bookCategoryService;
    @GetMapping("/list") public Result<List<CategoryVO>> listAll(@RequestParam(required = false) Integer status) { return Result.success(bookCategoryService.listAll(status)); }
    @GetMapping("/page")
    public Result<PageVO<CategoryVO>> listCategories(
            @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String categoryName, @RequestParam(required = false) Integer status) {
        return Result.success(bookCategoryService.listCategories(pageNum, pageSize, categoryName, status));
    }
    @GetMapping("/{id}") public Result<CategoryVO> getById(@PathVariable Long id) { return Result.success(bookCategoryService.getById(id)); }
    @PostMapping public Result<Void> addCategory(@Valid @RequestBody CategoryDTO dto) { bookCategoryService.addCategory(dto); return Result.success("新增成功", null); }
    @PutMapping public Result<Void> updateCategory(@Valid @RequestBody CategoryDTO dto) { bookCategoryService.updateCategory(dto); return Result.success("更新成功", null); }
    @DeleteMapping("/{id}") public Result<Void> deleteCategory(@PathVariable Long id) { bookCategoryService.deleteCategory(id); return Result.success("删除成功", null); }
}
