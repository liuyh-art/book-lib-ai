package com.example.book.service;
import com.example.book.dto.CategoryDTO;
import com.example.book.vo.CategoryVO;
import com.example.book.vo.PageVO;
import java.util.List;
public interface BookCategoryService {
    List<CategoryVO> listAll(Integer status);
    PageVO<CategoryVO> listCategories(int pageNum, int pageSize, String categoryName, Integer status);
    void addCategory(CategoryDTO dto);
    void updateCategory(CategoryDTO dto);
    void deleteCategory(Long id);
    CategoryVO getById(Long id);
}
