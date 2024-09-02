package backend.yamukja.review.controller;

import backend.yamukja.review.model.CreateReviewDto;
import backend.yamukja.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * User가 Place의 Review를 작성 후 Place의 Rating Update
     */
    @PostMapping("/review")
    public ResponseEntity<String> createScore(@RequestBody CreateReviewDto reviewDto) {
        return reviewService.createScore(reviewDto);
    }

}
