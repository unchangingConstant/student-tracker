package com.github.unchangingconstant.studenttracker.app.qrscan;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import org.cornutum.regexpgen.RegExpGen;
import org.cornutum.regexpgen.RegExpGenBuilder;
import org.cornutum.regexpgen.js.Provider;
import org.instancio.Instancio;

public class QRCodeTestUtils {

    /**
     * Randomly generates a random buffer up to 25 characters long containing the 
     * given substring.
     */
    public static LinkedBlockingDeque<Character> genBufferWith(String substr) {
        String randStr = Instancio.gen().string().length(substr.length(), 25).get();
        Integer index = (int) Math.round(Math.random() * (randStr.length() - substr.length()));
        String bufferStr = 
            randStr.substring(0, index) + substr 
            + randStr.substring(index + substr.length(), randStr.length());
        
        return bufferStr.chars()
            .mapToObj(c -> Character.valueOf((char) c))
            .collect(Collectors.toCollection(LinkedBlockingDeque::new));
    }

    public static RegExpGen regexGen(String regex) {
        return RegExpGenBuilder.generateRegExp(Provider.forEcmaScript()).exactly().matching(regex);
    }

}
