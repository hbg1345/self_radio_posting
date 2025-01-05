package com.example.selfRadioPosting.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;

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
}
