package com.prography.ping_pong.service.user;

import com.prography.ping_pong.domain.UserRoom;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.dto.request.user.FakerRequest;
import com.prography.ping_pong.dto.request.user.UserInitializeRequest;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.repository.RoomRepository;
import com.prography.ping_pong.repository.UserRepository;
import com.prography.ping_pong.repository.UserRoomRepository;
import com.prography.ping_pong.service.user.client.FakerUserDetail;
import com.prography.ping_pong.service.user.client.FakerUserDetails;
import com.prography.ping_pong.service.user.client.UserInitializeClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserInitializeClient userInitializeClient;

    public ApiResponse initialize(UserInitializeRequest userInitializeRequest) {
        truncateAllData();
        FakerRequest fakerRequest = new FakerRequest(userInitializeRequest.seed(), userInitializeRequest.quantity());
        FakerUserDetails userDetails = userInitializeClient.getUserDetails(fakerRequest);
        List<User> users = mapToUsers(userDetails.getFakerUserDetails());
        userRepository.saveAll(users); //TODO 벌크 쿼리로 변경
        return ApiResponse.ok();
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

    private void truncateAllData() {
        List<User> users = userRepository.findAll();
        List<Room> rooms = roomRepository.findAll();
        List<UserRoom> userRooms = userRoomRepository.findAll();

        userRoomRepository.deleteAllWithFlush(userRooms);
        roomRepository.deleteAllWithFlush(rooms);
        userRepository.deleteAllWithFlush(users);
    }
}
