package com.prography.ping_pong.service.user.client;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.prography.ping_pong.client.FakerResponseConverter;
import com.prography.ping_pong.dto.response.user.FakerResponse;
import com.prography.ping_pong.dto.response.user.FakerUserResponse;
import com.prography.ping_pong.exception.custom.PingPongServerException;
import com.prography.ping_pong.view.ResponseMessage;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;

class FakerResponseConverterTest {

    @DisplayName("응답이 null일 경우 서버 에러를 반환한다.")
    @Test
    void nullCheck() {
        FakerResponseConverter converter = new FakerResponseConverter();
        ConvertibleClientHttpResponse mockResponse = Mockito.mock(ConvertibleClientHttpResponse.class);

        assertThatThrownBy(() -> converter.convert(mockResponse))
                .isInstanceOf(PingPongServerException.class)
                .hasMessage(ResponseMessage.SERVER_ERROR.getValue());
    }

    @DisplayName("응답 코드가 200이 아닐 경우, 서버 에러를 반환한다.")
    @Test
    void validateResponseCode() {
        FakerResponseConverter converter = new FakerResponseConverter();
        FakerResponse mockBody = new FakerResponse("INVALID_REQUEST", 400, 0, 0, List.of());
        ConvertibleClientHttpResponse mockResponse = Mockito.mock(ConvertibleClientHttpResponse.class);

        Mockito.when(mockResponse.bodyTo(FakerResponse.class))
                .thenReturn(mockBody);

        assertThatThrownBy(() -> converter.convert(mockResponse))
                .isInstanceOf(PingPongServerException.class)
                .hasMessage(ResponseMessage.SERVER_ERROR.getValue());
    }

    @DisplayName("정상 응답일 경우 FakerUserDetails 로 매핑한다")
    @Test
    void convertUserDetails() {
        FakerResponseConverter converter = new FakerResponseConverter();
        FakerUserResponse userData1 = new FakerUserResponse(1L, "name1", "email1@email.com");
        FakerUserResponse userData2 = new FakerUserResponse(2L, "name2", "email2@email.com");
        FakerResponse mockBody = new FakerResponse("OK", 200, 1, 2, Arrays.asList(userData2, userData1));
        ConvertibleClientHttpResponse mockResponse = Mockito.mock(ConvertibleClientHttpResponse.class);

        Mockito.when(mockResponse.bodyTo(FakerResponse.class))
                .thenReturn(mockBody);

        assertThatCode(() -> converter.convert(mockResponse))
                .doesNotThrowAnyException();
    }
}
