package com.practice.devy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ToyMSACommonUtils {

    public String getRandomString() {
        log.info("called getRandomString");
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
