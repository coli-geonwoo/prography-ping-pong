package com.prography.pingpong.controller.swagger.annotation;

import com.prography.pingpong.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                        name = "서버 오류 예제",
                        value = "{ \"code\": 500, \"message\": \"에러가 발생했습니다\" }"
                )
        )
)
public @interface ErrorCode500 {

    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description() default "서버 내부 오류";
}
