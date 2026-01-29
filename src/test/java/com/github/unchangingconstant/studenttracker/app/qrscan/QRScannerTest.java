package com.github.unchangingconstant.studenttracker.app.qrscan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.instancio.Instancio.gen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.github.unchangingconstant.studenttracker.app.entities.StudentQRCode.*;
import com.github.unchangingconstant.studenttracker.app.dbmanager.DatabaseManager;

import static com.github.unchangingconstant.studenttracker.app.qrscan.QRCodeTestUtils.genBufferWith;


public class QRScannerTest {

    @Mock
    private DatabaseManager attendanceService;
    @Mock
    private KeyLogger keyLogger;

    @InjectMocks
    private QRScanner qrScanner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // TODO test the key event hook

    @Test
    @DisplayName("Successfully find QR when both id and checksum are one numeric digit")
    void testFindQRCode_1() {
        String qrCode = HEADER + gen().text().pattern("#d").get() + SEPERATOR + gen().text().pattern("#d").get() + FOOTER;
        String result = qrScanner.findQRCode(genBufferWith(qrCode));
        assertEquals(qrCode.toLowerCase(), result);
    }

    @Test
    @DisplayName("Successfully find QR when both id and checksum are one digit and the checksum is alphabetic")
    void testFindQRCode_2() {
        String qrCode = HEADER + gen().text().pattern("#d").get() + SEPERATOR + QRCodeTestUtils.genAlphaHexNumStr(1) + FOOTER;
        String result = qrScanner.findQRCode(genBufferWith(qrCode));
        assertEquals(qrCode.toLowerCase(), result);
    }

    @Test
    @DisplayName("Successfully find QR when both id and checksum are at least two numeric digits")
    void testFindQRCode_3() {
        String qrCode = HEADER + gen().text().pattern("#d#d").get() + SEPERATOR + gen().text().pattern("#d#d").get() + FOOTER;
        String result = qrScanner.findQRCode(genBufferWith(qrCode));
        assertEquals(qrCode.toLowerCase(), result);
    }

    @Test
    @DisplayName("Successfully find QR when both id and checksum are at least 2 digits and the checksum contains alphabetic characters")
    void testFindQRCode_4() {
        String qrCode = HEADER + gen().text().pattern("#d#d").get() + SEPERATOR + QRCodeTestUtils.genAlphaHexNumStr(2) + FOOTER;
        String result = qrScanner.findQRCode(genBufferWith(qrCode));
        assertEquals(qrCode.toLowerCase(), result);
    }

    @Test
    @DisplayName("Successfully find QR when studentId is max length")
    void testFindQRCode_5() {
        String qrCode = HEADER + gen().text().pattern("#d".repeat(10)).get() + SEPERATOR + QRCodeTestUtils.genAlphaHexNumStr(8) + FOOTER;
        String result = qrScanner.findQRCode(genBufferWith(qrCode));
        assertEquals(qrCode.toLowerCase(), result);
    }

}
