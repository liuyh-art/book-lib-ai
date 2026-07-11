package com.example.book.service.impl;
import com.example.book.common.BusinessException;
import com.example.book.common.ErrorCode;
import com.example.book.dto.CategoryDTO;
import com.example.book.entity.BookCategory;
import com.example.book.mapper.BookCategoryMapper;
import com.example.book.service.BookCategoryService;
import com.example.book.vo.CategoryVO;
import com.example.book.vo.PageVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class BookCategoryServiceImpl implements BookCategoryService {
    @Autowired private BookCategoryMapper bookCategoryMapper;
    @Override
    public List<CategoryVO> listAll(Integer status) {
        return bookCategoryMapper.selectAll(status).stream().map(this::convertToVO).collect(Collectors.toList());
    }
    @Override
    public PageVO<CategoryVO> listCategories(int pageNum, int pageSize, String categoryName, Integer status) {
        BookCategory query = new BookCategory();
        query.setCategoryName(categoryName); query.setStatus(status);
        PageHelper.startPage(pageNum, pageSize);
        List<BookCategory> list = bookCategoryMapper.selectList(query);
        PageInfo<BookCategory> pageInfo = new PageInfo<>(list);
        List<CategoryVO> voList = list.stream().map(this::convertToVO).collect(Collectors.toList());
        return new PageVO<>(voList, pageInfo.getTotal(), pageNum, pageSize, pageInfo.getPages());
    }
    @Override @Transactional
    public void addCategory(CategoryDTO dto) { BookCategory c = new BookCategory(); BeanUtils.copyProperties(dto, c); bookCategoryMapper.insert(c); }
    @Override @Transactional
    public void updateCategory(CategoryDTO dto) { BookCategory c = new BookCategory(); BeanUtils.copyProperties(dto, c); bookCategoryMapper.updateById(c); }
    @Override @Transactional
    public void deleteCategory(Long id) {
        if (bookCategoryMapper.countBooksByCategory(id) > 0) throw new BusinessException(ErrorCode.CATEGORY_USED);
        bookCategoryMapper.deleteById(id);
    }
    @Override
    public CategoryVO getById(Long id) {
        BookCategory category = bookCategoryMapper.selectById(id);
        if (category == null) throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        return convertToVO(category);
    }
    private CategoryVO convertToVO(BookCategory c) {
        CategoryVO vo = new CategoryVO(); BeanUtils.copyProperties(c, vo);
        vo.setCreateTime(c.getCreateTime() != null ? c.getCreateTime().toString().replace("T", " ") : null);
        vo.setUpdateTime(c.getUpdateTime() != null ? c.getUpdateTime().toString().replace("T", " ") : null);
        return vo;
    }
}
