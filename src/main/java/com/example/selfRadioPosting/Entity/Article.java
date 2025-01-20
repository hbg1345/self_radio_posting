package com.example.selfRadioPosting.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String category;
    @Column
    private String writer;
    @Column
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String videoUrl;
    @Column(columnDefinition = "TEXT")
    private String audioUrl;
    @Column
    private String created;
    @Column
    private String password;
    @Column
    private String ip;
    @Column
    private Long views;
    @Column
    private Long recommend;

    @PrePersist
    protected void onCreate() {
        if (views == null)
            views = 0L;
        if (recommend == null)
            recommend = 0L;
        if (created == null) {
            DateFormat outputFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            created = outputFormatter.format(new Date());
        }
    }
}
