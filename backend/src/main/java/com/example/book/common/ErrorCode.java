package com.example.book.common;

public enum ErrorCode {
    SUCCESS(200, "操作成功"), FAIL(500, "操作失败"), PARAM_ERROR(400, "参数错误"), UNAUTHORIZED(401, "未登录或token已过期"), FORBIDDEN(403, "无权限访问"),
    USER_NOT_FOUND(1001, "用户不存在"), USER_DISABLED(1002, "账号已被禁用"), USER_EXISTS(1003, "用户名已存在"), PASSWORD_ERROR(1004, "密码错误"), OLD_PASSWORD_ERROR(1005, "原密码错误"),
    BOOK_NOT_FOUND(2001, "图书不存在"), BOOK_OUT_OF_STOCK(2002, "库存不足"), BOOK_OFF_SHELF(2003, "图书已下架"),
    CATEGORY_NOT_FOUND(2101, "分类不存在"), CATEGORY_USED(2102, "分类下存在图书，无法删除"),
    BORROW_NOT_FOUND(3001, "借阅记录不存在"), BOOK_ALREADY_BORROWED(3002, "该书已借出"), BOOK_ALREADY_RETURNED(3003, "该书已归还"), BORROW_LIMIT(3004, "每人最多同时借阅5本书"),
    TOKEN_INVALID(4001, "token无效"), TOKEN_EXPIRED(4002, "token已过期");
    private final int code; private final String message;
    ErrorCode(int code, String message) { this.code = code; this.message = message; }
    public int getCode() { return code; } public String getMessage() { return message; }
}
