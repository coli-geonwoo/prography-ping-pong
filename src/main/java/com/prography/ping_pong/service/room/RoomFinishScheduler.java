package com.prography.ping_pong.service.room;

import com.prography.ping_pong.domain.room.Room;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
@RequiredArgsConstructor
public class RoomFinishScheduler {

    private static final long PING_PONG_GAME_INTERVAL_MINUTES = 1L;

    private final TaskScheduler taskScheduler;
    private final PlatformTransactionManager transactionManager;

    public void schedule(Room room) {
        Instant reserveTime = Instant.now().plus(PING_PONG_GAME_INTERVAL_MINUTES, ChronoUnit.MINUTES);
        taskScheduler.schedule(() -> finishRoom(room), reserveTime);
    }

    private void finishRoom(Room room) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            room.finished();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
        }
    }
}
