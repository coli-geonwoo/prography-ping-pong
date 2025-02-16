package com.prography.ping_pong.client;

import com.prography.ping_pong.dto.response.user.FakerResponse;
import com.prography.ping_pong.dto.response.user.FakerUserResponse;
import com.prography.ping_pong.exception.custom.PingPongServerException;
import com.prography.ping_pong.exception.errorcode.ServerErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;

@Component
public class FakerResponseConverter {

    private static final long OK_RESPONSE_CODE = 200;

    public FakerUserDetails convert(ConvertibleClientHttpResponse clientHttpResponse) {
        FakerResponse fakerResponse = clientHttpResponse.bodyTo(FakerResponse.class);
        validate(fakerResponse);
        return mapToUserDetails(fakerResponse.data());
    }

    private FakerUserDetails mapToUserDetails(List<FakerUserResponse> responses) {
        return responses.stream()
                .map(response -> new FakerUserDetail(response.id(), response.username(), response.email()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), FakerUserDetails::new));
    }

    private void validate(FakerResponse fakerResponse) {
        if (fakerResponse == null) {
            throw new PingPongServerException(ServerErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (fakerResponse.code() != OK_RESPONSE_CODE) {
            throw new PingPongServerException(ServerErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
