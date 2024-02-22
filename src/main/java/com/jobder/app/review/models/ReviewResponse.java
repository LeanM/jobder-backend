package com.jobder.app.review.models;

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
public class ReviewResponse {
    private String id;
    private String reviewId;
    private String workerId;

    private String content;

    private Date createdAt;
}
