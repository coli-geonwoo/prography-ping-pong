package com.prography.pingpong.controller.swagger.annotation;

import com.prography.pingpong.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ErrorCode201Container.class)
@ApiResponse(
        responseCode = "201",
        description = "클라이언트 입력 오류",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
)
public @interface ErrorCode201 {

    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description() default "클라이언트 입력 오류";
}
