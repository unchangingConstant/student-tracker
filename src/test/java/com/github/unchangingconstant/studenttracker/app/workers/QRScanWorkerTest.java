package com.github.unchangingconstant.studenttracker.app.workers;

import org.cornutum.regexpgen.random.RandomBoundsGen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.cornutum.regexpgen.RandomGen;
import org.cornutum.regexpgen.RegExpGen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.unchangingconstant.studenttracker.app.domain.StudentQRCodeDomain;
import com.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import com.github.unchangingconstant.studenttracker.app.services.KeyLoggerService;

import static com.github.unchangingconstant.studenttracker.app.workers.QRCodeTestUtils.genBufferWith;
import static com.github.unchangingconstant.studenttracker.app.workers.QRCodeTestUtils.regexGen;


public class QRScanWorkerTest {
    
    private final String HEADER = StudentQRCodeDomain.HEADER;
    private final String FOOTER = StudentQRCodeDomain.FOOTER;
    private final String SEPERATOR = StudentQRCodeDomain.SEPERATOR;
    private final RandomGen regexNumGen = new RandomBoundsGen();

    @Mock
    private AttendanceService attendanceServie;
    @Mock
    private KeyLoggerService keyLoggerService;

    @InjectMocks
    private QRScanWorker worker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // TODO test the key event hook
    /**
     * TODO
     * WHEN YOU WRITE MOCK QR CODES, MAKE SURE THEY ARE WITHIN 23 CHARACTERS!!!
     * ALSO, MAKE A CENTRAL SOURCE OF TRUTH FOR QR CODE FORMATS
     */

    @Test
    @DisplayName("Successfully find QR when both numbers are one numeric digit")
    void testFindQRCode_1() {
        // These next few lines generate a random QR code according to the regex I provide
        String regex = HEADER + "(\\d)" + SEPERATOR + "(\\d)" + FOOTER;
        RegExpGen gen = regexGen(regex);
        String qrCode = gen.generate(regexNumGen);
        
        String result = worker.findQRCode(genBufferWith(qrCode));

        assertEquals(qrCode.toLowerCase(), result);
    }

    @Test
    @DisplayName("Successfully find QR when both numbers are one digit and the hex number is alphabetic")
    void testFindQRCode_2() {
        String regex = HEADER + "(\\d)" + SEPERATOR + "([a-fA-F])" + FOOTER;
        RegExpGen gen = regexGen(regex);
        String qrCode = gen.generate(regexNumGen);
        
        String result = worker.findQRCode(genBufferWith(qrCode));

        assertEquals(qrCode.toLowerCase(), result);
    }

    @Test
    @DisplayName("Successfully find QR when both numbers are at least two numeric digits")
    void testFindQRCode_3() {
        String regex = HEADER + "([\\d]{2,10})" + SEPERATOR + "([\\d]{2,8})" + FOOTER;
        RegExpGen gen = regexGen(regex);
        String qrCode = gen.generate(regexNumGen);
        
        String result = worker.findQRCode(genBufferWith(qrCode));

        assertEquals(qrCode.toLowerCase(), result);
    }

    @Test
    @DisplayName("Successfully find QR when both numbers are at least 2 digits and the hex number contains alphabetic characters")
    void testFindQRCode_4() {
        String regex = HEADER + "([\\d]{2,10})" + SEPERATOR + "([0-9a-fA-F]{2,8})" + FOOTER;
        RegExpGen gen = regexGen(regex);
        String qrCode = gen.generate(regexNumGen);
        
        String result = worker.findQRCode(genBufferWith(qrCode));

        assertEquals(qrCode.toLowerCase(), result);
    }

    @Test
    @DisplayName("Successfully find QR when studentId is max length")
    void testFindQRCode_5() {
        String regex = HEADER + "([\\d]{10})" + SEPERATOR + "([0-9a-fA-F]{8})" + FOOTER;
        RegExpGen gen = regexGen(regex);
        String qrCode = gen.generate(regexNumGen);
        
        String result = worker.findQRCode(genBufferWith(qrCode));

        assertEquals(qrCode.toLowerCase(), result);
    }

}
