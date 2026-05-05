package com.ishaan.AlertSphere.entity;


import com.ishaan.AlertSphere.enums.Types;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "alerts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Alert {

    @Id
    private ObjectId id;
    @NotNull
    private String message;
    @NotNull
    private Types type;
    private LocalDateTime triggerTime;

    @NotNull
    @Indexed
    @Field("userId")
    @DocumentReference(lazy = true)
    private User user;

}
