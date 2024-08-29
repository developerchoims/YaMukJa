package backend.yamukja.common.exception;

import backend.yamukja.common.dto.ErrorResponseDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class GeneralException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public GeneralException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }

    // ErrorResponseDto로 변환
    public ErrorResponseDto toErrorResponseDto() {
        return new ErrorResponseDto(this.message, this.status);
    }
}