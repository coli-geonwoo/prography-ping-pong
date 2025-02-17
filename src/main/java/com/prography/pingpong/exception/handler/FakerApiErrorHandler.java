package com.prography.pingpong.exception.handler;

import com.prography.pingpong.exception.custom.PingPongServerException;
import com.prography.pingpong.exception.errorcode.ServerErrorCode;
import java.io.IOException;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Slf4j
@Component
public class FakerApiErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError()
                || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        log.error("response status code is {}", response.getStatusCode());
        throw new PingPongServerException(ServerErrorCode.INTERNAL_SERVER_ERROR);
    }
}
