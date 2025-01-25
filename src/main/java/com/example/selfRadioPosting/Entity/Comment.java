package com.example.selfRadioPosting.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@ToString
@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="article_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article article;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column
    private String writer;
    @Column
    private String ip;
    @Column
    private String password;
    @Column
    private String created;

    @PrePersist
    protected void onCreate() {
        if (created == null) {
            DateFormat outputFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            created = outputFormatter.format(new Date());
        }
    }
}
