package com.prography.pingpong.service.user.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.prography.pingpong.client.FakerResponseConverter;
import com.prography.pingpong.client.FakerUserDetails;
import com.prography.pingpong.client.PrographyProperties;
import com.prography.pingpong.client.UserInitializeClient;
import com.prography.pingpong.dto.request.user.FakerRequest;
import com.prography.pingpong.exception.custom.PingPongServerException;
import com.prography.pingpong.view.ResponseMessage;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Import({FakerResponseConverter.class})
@RestClientTest(UserInitializeClient.class)
@EnableConfigurationProperties(PrographyProperties.class)
class UserInitializeClientTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Autowired
    private PrographyProperties properties;

    @Autowired
    private FakerResponseConverter responseConverter;

    private UserInitializeClient userInitializeClient;

    @BeforeEach
    void setUp() {
        this.mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        this.userInitializeClient = new UserInitializeClient(responseConverter, properties, restClientBuilder);
    }

    @DisplayName("정상응답 시 유저 정보가 제대로 바인딩 된다")
    @Test
    void initializeUserDetails() throws IOException {
        FakerRequest request = new FakerRequest(1L, 10L);
        String response = makeResponseByPath("faker/success.json");
        setMockServer(request, withSuccess(response, MediaType.APPLICATION_JSON));

        FakerUserDetails userDetails = userInitializeClient.getUserDetails(request);

        assertAll(
                () -> assertThat(userDetails.getFakerUserDetails()).hasSize(10),
                () -> mockServer.verify()
        );
    }

    @DisplayName("faker 서버에서 4XX 에러 반환 시 커스텀 서버 에러로 전환한다")
    @Test
    void throwClientError() {
        FakerRequest request = new FakerRequest(1L, 10L);

        setMockServer(request, withBadRequest());

        assertThatThrownBy(() -> userInitializeClient.getUserDetails(request))
                .isInstanceOf(PingPongServerException.class)
                .hasMessage(ResponseMessage.SERVER_ERROR.getValue());
    }

    @DisplayName("faker 서버에서 5XX 에러 반환 시 커스텀 서버 에러로 전환한다")
    @Test
    void throwServerError() {
        FakerRequest request = new FakerRequest(1L, 10L);

        setMockServer(request, withServerError());

        assertThatThrownBy(() -> userInitializeClient.getUserDetails(request))
                .isInstanceOf(PingPongServerException.class)
                .hasMessage(ResponseMessage.SERVER_ERROR.getValue());
    }

    private void setMockServer(
            FakerRequest request,
            ResponseCreator responseCreator
    ) {
        String requestUri = makeUri(request);
        mockServer.expect(requestTo(requestUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(responseCreator);
    }

    private String makeUri(FakerRequest request) {
        return UriComponentsBuilder.fromUriString(properties.baseUrl())
                .queryParam("_seed", request.seed())
                .queryParam("_quantity", request.quantity())
                .queryParam("_locale", properties.locale())
                .build(false)
                .toUriString();
    }

    private String makeResponseByPath(String path) throws IOException {
        return new String(Files.readAllBytes(
                new ClassPathResource(path).getFile().toPath())
        );
    }
}
