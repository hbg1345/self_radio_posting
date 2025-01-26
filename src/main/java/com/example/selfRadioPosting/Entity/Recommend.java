package com.example.selfRadioPosting.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Recommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="article_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article article;
    @Column
    private String ip;
    @Column(name="recommend_date")
    private String recommendDate;

    @PrePersist
    protected void onCreate() {
        if (recommendDate == null) {
            DateFormat outputFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            recommendDate= outputFormatter.format(new Date());
        }
    }
}

