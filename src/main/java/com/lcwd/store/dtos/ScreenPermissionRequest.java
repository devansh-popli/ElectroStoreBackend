package com.lcwd.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenPermissionRequest {
    private String screenName;
    private boolean canRead;
    private boolean canWrite;
    private boolean canUpdate;
    private boolean canDelete;

    // getters and setters
}
