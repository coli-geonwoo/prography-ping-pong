package com.prography.pingpong.domain.user;

import com.prography.pingpong.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "member")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private long fakerId;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotNull
    @Column(columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public User(long fakerId, String name, String email, UserStatus status) {
        this(null, fakerId, name, email, status);
    }

    public boolean isActive() {
        return this.status.isActive();
    }
}
