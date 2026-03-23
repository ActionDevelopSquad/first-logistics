package common.exception;

import common.response.ApiResponse;
import common.response.CommonErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
		log.warn("[{}] code: {}, message: {}", e.getClass().getSimpleName(), e.getErrorCode().getCode(), e.getMessage());
		return ResponseEntity
				.status(e.getErrorCode().getStatus())
				.body(ApiResponse.error(e.getErrorCode()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<String>> handleValidException(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining(", "));
		log.warn("[MethodArgumentNotValidException] message: {}", message);
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(CommonErrorCode.INVALID_INPUT, message));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
		String message = String.format("'%s' 파라미터의 값이 올바르지 않습니다: %s", e.getName(), e.getValue());
		log.warn("[MethodArgumentTypeMismatchException] {}", message);
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(CommonErrorCode.INVALID_INPUT, message));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<String>> handleConstraintViolation(ConstraintViolationException e) {
		String message = e.getConstraintViolations().stream()
				.map(v -> v.getPropertyPath() + ": " + v.getMessage())
				.collect(Collectors.joining(", "));
		log.warn("[ConstraintViolationException] message: {}", message);
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(CommonErrorCode.INVALID_INPUT, message));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
		log.warn("[HttpMessageNotReadableException] message: {}", e.getMessage());
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(CommonErrorCode.INVALID_INPUT, "요청 본문을 읽을 수 없습니다."));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		log.error("[Exception] message: {}", e.getMessage(), e);
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR));
	}
}