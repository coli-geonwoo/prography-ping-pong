package com.prography.ping_pong.service.user;

import com.prography.ping_pong.client.FakerUserDetail;
import com.prography.ping_pong.client.FakerUserDetails;
import com.prography.ping_pong.client.UserInitializeClient;
import com.prography.ping_pong.domain.userroom.UserRoom;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.dto.request.user.FakerRequest;
import com.prography.ping_pong.dto.request.user.UserInitializeRequest;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.exception.custom.PingPongClientErrorException;
import com.prography.ping_pong.exception.errorcode.ClientErrorCode;
import com.prography.ping_pong.repository.RoomRepository;
import com.prography.ping_pong.repository.UserRepository;
import com.prography.ping_pong.repository.UserRoomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserInitializeClient userInitializeClient;
    private final UserRepository userRepository;

    @Transactional
    public List<User> initialize(long seed, long quantity) {
        FakerRequest fakerRequest = new FakerRequest(seed, quantity);
        FakerUserDetails userDetails = userInitializeClient.getUserDetails(fakerRequest);
        List<User> users = mapToUsers(userDetails.getFakerUserDetails());
        return userRepository.saveAll(users);
    }

    private List<User> mapToUsers(List<FakerUserDetail> userDetails) {
        return userDetails.stream()
                .map(userDetail -> new User(
                        userDetail.getFakerId(),
                        userDetail.getName(),
                        userDetail.getEmail(),
                        userDetail.getStatus())
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAllByOrderByIdAsc(pageable);
    }

    @Transactional(readOnly = true)
    public User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST));
    }

    @Transactional
    public void deleteAllUsers() {
        List<User> users = userRepository.findAll();
        userRepository.deleteAllWithFlush(users);
    }
}
