package com.duck.dto.review;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewStarNumber {
    private int all;
    private int oneStar;
    private int twoStar;
    private int threeStar;
    private int fourStar;
    private int fiveStar;
}
