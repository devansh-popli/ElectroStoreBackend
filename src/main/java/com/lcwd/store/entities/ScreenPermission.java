package com.lcwd.store.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScreenPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String screenName;
    private boolean canRead;
    private boolean canWrite;
    private boolean canUpdate;
    private boolean canDelete;

    // getters and setters
}
