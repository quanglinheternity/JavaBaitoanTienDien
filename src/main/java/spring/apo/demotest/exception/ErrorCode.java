package spring.apo.demotest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_ERROR(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ID_KEY(1001, "ID không hợp lệ", HttpStatus.BAD_REQUEST),

    // Authentication
    AUTHENTICATION_FAILED(2001, "Sai tài khoản hoặc mật khẩu", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_REQUIRED(2002, "Bạn chưa đăng nhập", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(2003, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2004, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    INVALID_VERIFICATION_CODE(1014, "Số xác minh không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_CODE_EXPIRED(1015, "Số xác minh hết hạn", HttpStatus.BAD_REQUEST),
    USER_ALREADY_VERIFIED(1016, "Người dùng đã được xác minh", HttpStatus.BAD_REQUEST),
    USER_NOT_VERIFIED(1017, "Người dùng chưa được xác minh", HttpStatus.BAD_REQUEST),

    USAGE_ALREADY_EXISTS(1100, "Người đùng đã có số tiền trong tháng", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DELETED(1100, "Người dùng được xóa", HttpStatus.BAD_REQUEST),
    USER_NOT_DELETED(1100, "Người dùng chưa bị xoá", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(1101, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_INVALID_PASSWORD(1102, "Mật khẩu phải có ít nhất 8 ký tự", HttpStatus.BAD_REQUEST),
    USER_INVALID_USERNAME(1103, "Tên đăng nhập phải có ít nhất 5 ký tự", HttpStatus.BAD_REQUEST),
    USER_USERNAME_NOT_FOUND(1105, "Tên đăng không được để trống", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1104, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),

    // 400 - Bad Request
    INVALID_REQUEST(1000, "Request không hợp lệ", HttpStatus.BAD_REQUEST),
    TIER_CONFIG_NAME_UNIQUE(1001, "Tên tier không được trùng", HttpStatus.BAD_REQUEST),
    TIER_CONFIG_NAME_NOT_FOUND(1001, "Tên tier không được để trống", HttpStatus.BAD_REQUEST),
    TIER_CONFIG_NAME_INVALID_SIZE(1002, "Tên tier phải từ 1 đến 50 ký tự", HttpStatus.BAD_REQUEST),
    TIER_CONFIG_MIN_VALUE_REQUIRED(1003, "Giá trị min không được null", HttpStatus.BAD_REQUEST),
    TIER_CONFIG_MIN_VALUE_INVALID(1004, "Giá trị min không hợp lệ", HttpStatus.BAD_REQUEST),
    TIER_CONFIG_MAX_VALUE_REQUIRED(1005, "Giá trị max không được null", HttpStatus.BAD_REQUEST),
    TIER_CONFIG_MAX_VALUE_INVALID(1006, "Giá trị max không hợp lệ", HttpStatus.BAD_REQUEST),
    TIER_CONFIG_PRICE_REQUIRED(1007, "Giá không được null", HttpStatus.BAD_REQUEST),
    TIER_CONFIG_PRICE_INVALID(1008, "Giá phải lớn hơn 0", HttpStatus.BAD_REQUEST),

    DATE_REQUIRED(1009, "Ngày không được để trống", HttpStatus.BAD_REQUEST),
    DATE_INVALID_FORMAT(1010, "Ngày phải theo định dạng yyyy-MM-dd", HttpStatus.BAD_REQUEST),
    KWH_REQUIRED(1011, "Số điện không được để trống", HttpStatus.BAD_REQUEST),
    KWH_MUST_BE_GREATER_THAN_ZERO(1012, "Số điện phải lớn hơn 0", HttpStatus.BAD_REQUEST),
    USAGE_NOT_FOUND(1013, "Tháng số điện không tồn tại", HttpStatus.NOT_FOUND),
    ;
    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
