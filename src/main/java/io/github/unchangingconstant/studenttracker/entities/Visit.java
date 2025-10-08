package io.github.unchangingconstant.studenttracker.entities;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Visit {

    @NonNull
    private Integer visitId;
    @NonNull
    private Integer studentId;
    @NonNull
    private LocalDateTime startTime;
    // If null, the visit is ongoing
    private LocalDateTime endTime;

}
