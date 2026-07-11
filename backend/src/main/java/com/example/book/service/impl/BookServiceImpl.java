package com.example.book.service.impl;
import com.example.book.common.BusinessException;
import com.example.book.common.ErrorCode;
import com.example.book.dto.BookDTO;
import com.example.book.entity.Book;
import com.example.book.entity.BookCategory;
import com.example.book.mapper.BookCategoryMapper;
import com.example.book.mapper.BookMapper;
import com.example.book.service.BookService;
import com.example.book.vo.BookVO;
import com.example.book.vo.PageVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class BookServiceImpl implements BookService {
    @Autowired private BookMapper bookMapper;
    @Autowired private BookCategoryMapper bookCategoryMapper;
    @Override
    public PageVO<BookVO> listBooks(int pageNum, int pageSize, String bookName, String author, Long categoryId, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<Book> list = bookMapper.selectPage(bookName, author, categoryId, status);
        PageInfo<Book> pageInfo = new PageInfo<>(list);
        Map<Long, String> categoryMap = buildCategoryMap(list);
        List<BookVO> voList = list.stream().map(book -> convertToVO(book, categoryMap)).collect(Collectors.toList());
        return new PageVO<>(voList, pageInfo.getTotal(), pageNum, pageSize, pageInfo.getPages());
    }
    @Override @Transactional
    public void addBook(BookDTO dto) {
        Book book = new Book(); BeanUtils.copyProperties(dto, book); bookMapper.insert(book);
    }
    @Override @Transactional
    public void updateBook(BookDTO dto) { Book book = new Book(); BeanUtils.copyProperties(dto, book); bookMapper.updateById(book); }
    @Override @Transactional
    public void deleteBook(Long id) { bookMapper.deleteById(id); }
    @Override
    public BookVO getById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        return convertToVO(book, null);
    }
    @Override
    public List<BookVO> listAll() { return bookMapper.selectAll().stream().map(b -> convertToVO(b, null)).collect(Collectors.toList()); }
    private Map<Long, String> buildCategoryMap(List<Book> books) {
        if (books == null || books.isEmpty()) return Collections.emptyMap();
        return bookCategoryMapper.selectAll(1).stream().collect(Collectors.toMap(BookCategory::getId, BookCategory::getCategoryName));
    }
    private BookVO convertToVO(Book book, Map<Long, String> categoryMap) {
        BookVO vo = new BookVO(); BeanUtils.copyProperties(book, vo);
        if (categoryMap != null) { String name = categoryMap.get(book.getCategoryId()); if (name != null) vo.setCategoryName(name); }
        else { BookCategory c = bookCategoryMapper.selectById(book.getCategoryId()); if (c != null) vo.setCategoryName(c.getCategoryName()); }
        vo.setCreateTime(book.getCreateTime() != null ? book.getCreateTime().toString().replace("T", " ") : null);
        vo.setUpdateTime(book.getUpdateTime() != null ? book.getUpdateTime().toString().replace("T", " ") : null);
        return vo;
    }
}
