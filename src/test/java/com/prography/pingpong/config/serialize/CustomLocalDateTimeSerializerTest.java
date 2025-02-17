package com.prography.pingpong.config.serialize;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class CustomLocalDateTimeSerializerTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private JacksonTester<LocalDateTime> localDateTimeJacksonTester;

    @DisplayName("LocalDateTime은 yyyy-MM-dd HH:mm:ss 형식으로 직렬화된다")
    @Test
    void serialize() throws IOException {
        LocalDateTime now = LocalDateTime.now();

        JsonContent<LocalDateTime> result = localDateTimeJacksonTester.write(now);

        assertThat(result).extractingJsonPathStringValue("$")
                .isEqualTo(DATE_TIME_FORMATTER.format(now));
    }
}
