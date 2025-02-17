package com.prography.pingpong.dto.response;

import com.prography.pingpong.view.ResponseMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiBodyResponse<T> {

    @Schema(description = "응답 코드", example = "200")
    private Integer code;

    @Schema(description = "응답 메시지", example = "API 요청이 성공했습니다.")
    private String message;

    private T result;

    public static <T> ApiBodyResponse<T> ok(T body) {
        return new ApiBodyResponse<>(
                HttpStatus.OK.value(),
                ResponseMessage.SUCCESS.getValue(),
                body
        );
    }
}
