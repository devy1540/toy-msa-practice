package com.practice.devy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ToyMSACommonUtils {

    public String getRandomString() {
        log.info("called getRandomString");
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
