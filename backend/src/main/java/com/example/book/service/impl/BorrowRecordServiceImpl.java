package com.example.book.service.impl;
import com.example.book.common.BusinessException;
import com.example.book.common.ErrorCode;
import com.example.book.entity.Book;
import com.example.book.entity.BookCategory;
import com.example.book.entity.BorrowRecord;
import com.example.book.entity.SysUser;
import com.example.book.mapper.BookCategoryMapper;
import com.example.book.mapper.BookMapper;
import com.example.book.mapper.BorrowRecordMapper;
import com.example.book.mapper.SysUserMapper;
import com.example.book.service.BorrowRecordService;
import com.example.book.vo.BorrowRecordVO;
import com.example.book.vo.DashboardVO;
import com.example.book.vo.PageVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {
    @Autowired private BorrowRecordMapper borrowRecordMapper;
    @Autowired private BookMapper bookMapper;
    @Autowired private SysUserMapper sysUserMapper;
    @Autowired private BookCategoryMapper bookCategoryMapper;
    @Autowired(required = false) private Counter borrowCounter;
    @Autowired(required = false) private Counter returnCounter;

    @Override @Transactional
    public void borrowBook(Long userId, Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        if (book.getStatus() == 0) throw new BusinessException(ErrorCode.BOOK_OFF_SHELF);
        long borrowingCount = borrowRecordMapper.countBorrowingByUser(userId);
        if (borrowingCount >= 5) throw new BusinessException(ErrorCode.BORROW_LIMIT);
        if (borrowRecordMapper.selectBorrowing(userId, bookId) != null) throw new BusinessException(ErrorCode.BOOK_ALREADY_BORROWED);
        int updated = bookMapper.decrementStock(bookId);
        if (updated == 0) throw new BusinessException(ErrorCode.BOOK_OUT_OF_STOCK, "库存不足");
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId); record.setBookId(bookId);
        borrowRecordMapper.insert(record);
        if (borrowCounter != null) borrowCounter.increment();
    }

    @Override @Transactional
    public void returnBook(Long userId, Long bookId) {
        BorrowRecord record = borrowRecordMapper.selectBorrowing(userId, bookId);
        if (record == null) throw new BusinessException(ErrorCode.BORROW_NOT_FOUND);
        if (record.getStatus() == 1) throw new BusinessException(ErrorCode.BOOK_ALREADY_RETURNED);
        BorrowRecord update = new BorrowRecord();
        update.setId(record.getId()); update.setStatus(1); update.setReturnTime(LocalDateTime.now());
        borrowRecordMapper.updateById(update);
        bookMapper.incrementStock(bookId);
        if (returnCounter != null) returnCounter.increment();
    }

    @Override
    public PageVO<BorrowRecordVO> listRecords(int pageNum, int pageSize, Long userId, Long bookId, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<BorrowRecord> list = borrowRecordMapper.selectPage(userId, bookId, status);
        PageInfo<BorrowRecord> pageInfo = new PageInfo<>(list);
        Map<Long, SysUser> userMap = buildUserMap(list);
        Map<Long, Book> bookMap = buildBookMap(list);
        List<BorrowRecordVO> voList = list.stream().map(r -> convertToVO(r, userMap, bookMap)).collect(Collectors.toList());
        return new PageVO<>(voList, pageInfo.getTotal(), pageNum, pageSize, pageInfo.getPages());
    }

    @Override
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();
        vo.setTotalUsers((long) sysUserMapper.selectList(null).size());
        vo.setTotalBooks((long) bookMapper.selectAll().size());
        vo.setTotalCategories((long) bookCategoryMapper.selectAll(1).size());
        vo.setBorrowedBooks(borrowRecordMapper.countTotalBorrowed());
        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.atTime(LocalTime.MAX);
        vo.setTodayBorrows(borrowRecordMapper.countBorrowBetween(dayStart, dayEnd));
        vo.setTodayReturns(borrowRecordMapper.countReturnBetween(dayStart, dayEnd));
        return vo;
    }

    private Map<Long, SysUser> buildUserMap(List<BorrowRecord> records) {
        if (records == null || records.isEmpty()) return Collections.emptyMap();
        return sysUserMapper.selectList(null).stream().collect(Collectors.toMap(SysUser::getId, u -> u));
    }
    private Map<Long, Book> buildBookMap(List<BorrowRecord> records) {
        if (records == null || records.isEmpty()) return Collections.emptyMap();
        return bookMapper.selectAll().stream().collect(Collectors.toMap(Book::getId, b -> b));
    }
    private BorrowRecordVO convertToVO(BorrowRecord record, Map<Long, SysUser> userMap, Map<Long, Book> bookMap) {
        BorrowRecordVO vo = new BorrowRecordVO();
        vo.setId(record.getId()); vo.setUserId(record.getUserId()); vo.setBookId(record.getBookId()); vo.setStatus(record.getStatus());
        SysUser user = userMap != null ? userMap.get(record.getUserId()) : null;
        if (user != null) { vo.setUserName(user.getUsername()); vo.setRealName(user.getRealName()); }
        Book book = bookMap != null ? bookMap.get(record.getBookId()) : null;
        if (book != null) { vo.setBookName(book.getBookName()); vo.setAuthor(book.getAuthor()); vo.setIsbn(book.getIsbn()); }
        vo.setBorrowTime(record.getBorrowTime() != null ? record.getBorrowTime().toString().replace("T", " ") : null);
        vo.setReturnTime(record.getReturnTime() != null ? record.getReturnTime().toString().replace("T", " ") : null);
        vo.setCreateTime(record.getCreateTime() != null ? record.getCreateTime().toString().replace("T", " ") : null);
        return vo;
    }
}
