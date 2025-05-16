package com.example.Tokkit_server.wallet.utils;

import java.util.Random;

public class AccountGenerator {
    private static final String WOORI_PREFIX = "1002";

    public static String generateAccountNumber() {
        Random rand = new Random();

        // 중간 3자리: 100~999 사이 (너무 작은 수 방지)
        int middle = 100 + rand.nextInt(900);

        // 마지막 6자리: 100000~999999 사이 (0으로 시작하지 않게)
        int last = 100000 + rand.nextInt(900000);

        return String.format("%s-%03d-%06d", WOORI_PREFIX, middle, last);
    }
}
