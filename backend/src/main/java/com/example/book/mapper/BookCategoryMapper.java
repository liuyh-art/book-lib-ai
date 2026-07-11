package com.example.book.mapper;
import com.example.book.entity.BookCategory;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface BookCategoryMapper {
    BookCategory selectById(@Param("id") Long id);
    List<BookCategory> selectAll(@Param("status") Integer status);
    List<BookCategory> selectList(@Param("category") BookCategory category);
    long countTotal(@Param("category") BookCategory category);
    long countBooksByCategory(@Param("categoryId") Long categoryId);
    int insert(BookCategory category);
    int updateById(BookCategory category);
    int deleteById(@Param("id") Long id);
}
