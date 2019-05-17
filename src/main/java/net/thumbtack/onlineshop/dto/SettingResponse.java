package net.thumbtack.onlineshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingResponse {
    private int maxNameLength;
    private int minPasswordLength;
}
