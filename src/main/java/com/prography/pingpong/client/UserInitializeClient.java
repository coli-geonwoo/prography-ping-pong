package com.prography.pingpong.client;

import com.prography.pingpong.dto.request.user.FakerRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@EnableConfigurationProperties(PrographyProperties.class)
public class UserInitializeClient {

    private final FakerResponseConverter responseConverter;
    private final PrographyProperties properties;
    private final RestClient restClient;

    public UserInitializeClient(
            FakerResponseConverter responseConverter,
            PrographyProperties properties,
            RestClient.Builder restClientBuilder
    ) {
        this.responseConverter = responseConverter;
        this.properties = properties;
        this.restClient = restClientBuilder.build();
    }

    public FakerUserDetails getUserDetails(FakerRequest request) {
        return restClient.get()
                .uri(makeUserDataUrl(request.seed(), request.quantity()))
                .exchange((req, res) -> responseConverter.convert(res));
    }

    private String makeUserDataUrl(long seed, long quantity) {
        return UriComponentsBuilder.fromUriString(properties.baseUrl())
                .queryParam("_seed", seed)
                .queryParam("_quantity", quantity)
                .queryParam("_locale", properties.locale())
                .build(false)
                .toUriString();
    }
}
