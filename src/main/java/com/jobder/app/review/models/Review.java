package com.jobder.app.review.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Review {

    private String id;
    private String clientId;
    private String workerId;

    private String content;
    private Float rating;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;
}
