package com.example.book.mapper;
import com.example.book.entity.Book;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface BookMapper {
    Book selectById(@Param("id") Long id);
    Book selectByIsbn(@Param("isbn") String isbn);
    List<Book> selectPage(@Param("bookName") String bookName, @Param("author") String author, @Param("categoryId") Long categoryId, @Param("status") Integer status);
    long countTotal(@Param("bookName") String bookName, @Param("author") String author, @Param("categoryId") Long categoryId, @Param("status") Integer status);
    int insert(Book book);
    int updateById(Book book);
    int deleteById(@Param("id") Long id);
    int decrementStock(@Param("id") Long id);
    int incrementStock(@Param("id") Long id);
    List<Book> selectAll();
}
