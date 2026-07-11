package com.example.book.mapper;
import com.example.book.entity.BorrowRecord;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
public interface BorrowRecordMapper {
    BorrowRecord selectById(@Param("id") Long id);
    List<BorrowRecord> selectPage(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("status") Integer status);
    long countTotal(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("status") Integer status);
    long countBorrowingByUser(@Param("userId") Long userId);
    BorrowRecord selectBorrowing(@Param("userId") Long userId, @Param("bookId") Long bookId);
    int insert(BorrowRecord record);
    int updateById(BorrowRecord record);
    int deleteById(@Param("id") Long id);
    long countTotalBorrowed();
    long countBorrowBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    long countReturnBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    List<Map<String, Object>> countByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 查询当前用户借阅的书
     * @param userId
     * @return
     */
    List<String> countBorrowingByUserId(@Param("userId")Long userId);

    /**
     * 逾期未还得书
     * @param userId
     * @return
     */
    List<String> selectOverdueBook(@Param("userId")Long userId);

    List<String> selectCanBorrowBook(@Param("category")String category);

    Integer selectRemainBorrowNum(@Param("userId")Long userId);
}
