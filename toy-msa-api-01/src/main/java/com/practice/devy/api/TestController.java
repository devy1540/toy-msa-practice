package com.practice.devy.api;

import com.practice.devy.ToyMSACommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final ToyMSACommonUtils commonUtils;

    @GetMapping("get-test-01")
    public ResponseEntity<String> getTest01() {
        String str = commonUtils.getRandomString();

        log.info("called API get-test-01");

        return ResponseEntity.ok(str);
    }
}
