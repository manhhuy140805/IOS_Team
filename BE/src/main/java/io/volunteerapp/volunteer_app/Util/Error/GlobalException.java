// package io.volunteerapp.volunteer_app.Util.Error;

// import com.HieuVo.FashionStore_BackEnd.DTO.Response.RestResponse;
// import io.jsonwebtoken.security.SignatureException;
// import jakarta.validation.ConstraintViolationException;
// import org.apache.coyote.BadRequestException;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.BindingResult;
// import org.springframework.validation.FieldError;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.stream.Collectors;

// @RestControllerAdvice
// public class GlobalException {
// @ExceptionHandler(value = { MainException.class,
// Exception.class, // một mình cái này là lấy full lỗi r
// BadRequestException.class })
// public ResponseEntity<RestResponse<Object>> handleMainException(Exception
// exception) {
// RestResponse<Object> restResponse = new RestResponse<>();
// restResponse.setStatus(HttpStatus.BAD_REQUEST.value());
// restResponse.setError(exception.getMessage());
// restResponse.setMessage("Error occurred");
// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
// }

// @ExceptionHandler(MethodArgumentNotValidException.class)
// public ResponseEntity<RestResponse<Object>>
// validationError(MethodArgumentNotValidException ex) {
// BindingResult result = ex.getBindingResult();
// final List<FieldError> fieldErrors = result.getFieldErrors();

// RestResponse<Object> res = new RestResponse<>();
// res.setStatus(HttpStatus.BAD_REQUEST.value());

// // Lọc lỗi và lấy thông tin lỗi
// List<String> errors = fieldErrors.stream()
// .map(error -> error.getField() + ": " + error.getDefaultMessage())
// .collect(Collectors.toList());

// res.setError(errors);
// res.setMessage("Validation error");

// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
// }

// @ExceptionHandler(ConstraintViolationException.class)
// public ResponseEntity<RestResponse<Object>>
// handleConstraintViolationException(ConstraintViolationException ex) {
// RestResponse<Object> res = new RestResponse<>();
// res.setStatus(HttpStatus.BAD_REQUEST.value());

// // Lọc lỗi và lấy thông tin lỗi
// List<String> errors = ex.getConstraintViolations().stream()
// .map(violation -> violation.getPropertyPath() + ": " +
// violation.getMessage())
// .collect(Collectors.toList());

// res.setMessage(errors.size() > 1 ? errors : errors.get(0)); // nếu có một lỗi
// thì String

// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
// }

// }
