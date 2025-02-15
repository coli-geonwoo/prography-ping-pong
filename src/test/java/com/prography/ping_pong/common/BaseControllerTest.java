package com.prography.ping_pong.common;

import com.prography.ping_pong.repository.RoomRepository;
import com.prography.ping_pong.repository.UserRepository;
import com.prography.ping_pong.repository.UserRoomRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@ExtendWith(DataBaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoomRepository roomRepository;

    @Autowired
    protected UserRoomRepository userRoomRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setEnvironment() {
        RestAssured.port = port;
    }
}
