package org.example.entity;


import lombok.*;
import org.telegram.telegrambots.meta.api.objects.Update;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class RawData {
    private Update event;
}
