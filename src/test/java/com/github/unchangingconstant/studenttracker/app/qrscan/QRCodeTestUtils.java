package com.github.unchangingconstant.studenttracker.app.qrscan;

import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import org.instancio.Instancio;

public class QRCodeTestUtils {

    private static final Random RANDOM = new Random();
    private static final String[] HEX_ALPHA_CHARS =
        {"a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F"};

    /**
     * Randomly generates a random buffer up to 26 characters long containing the
     * given substring.
     */
    public static LinkedBlockingDeque<Character> genBufferWith(String substr) {
        String randStr = Instancio.gen().string().length(substr.length(), 26).get();
        int index = (int) Math.round(Math.random() * (randStr.length() - substr.length()));
        String bufferStr = 
            randStr.substring(0, index) + substr + randStr.substring(index + substr.length());
        
        return bufferStr.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toCollection(LinkedBlockingDeque::new));
    }

    // Creates a hex number with at least one alphabetic character
    // You'd think instancio would cover something like this
    public static String genAlphaHexNumStr(int length) {
        if (length == 0) return "";

        StringBuilder base = new StringBuilder("0".repeat(length));
        String randHex = Integer.toHexString(RANDOM.nextInt(16 ^ length));
        base.replace(0, randHex.length(), randHex);
        int mandatoryAlphaCharIndex = RANDOM.nextInt(length);
        base.replace(
            mandatoryAlphaCharIndex, mandatoryAlphaCharIndex + 1,
            HEX_ALPHA_CHARS[RANDOM.nextInt(HEX_ALPHA_CHARS.length)]
        );

        return base.toString();
    }

}
