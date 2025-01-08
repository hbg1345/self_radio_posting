package com.example.selfRadioPosting.util;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Pair<F, S> {
    private F first;
    private S second;
}
