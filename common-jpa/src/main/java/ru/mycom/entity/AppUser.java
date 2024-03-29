package ru.mycom.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.mycom.entity.enums.UserState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long telegramUserId;
    @CreationTimestamp
    private LocalDateTime firstLocalDate;
    private String firtName;
    private String lastName;
    private String userName;
    private String email;
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private UserState userState;
}
