package dev.ridill.oar.transactions.domain.autoDetection

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class RegexTransactionDataExtractorTest {

    private lateinit var extractor: TransactionDataExtractor
    private lateinit var messageList: List<String>

    @Before
    fun setUp() {
        extractor = RegexTransactionDataExtractor()
        messageList = listOf(
            """Amt Sent Rs.250.00
                From HDFC Bank A/C *1643
                To H R FRUITS
                On 27-03
                Ref 408718670922 
                Not You? Call 18002586161/SMS BLOCK UPI to 7308080808
            """.trimIndent(),
            """Amt Sent Rs.3000.00
From HDFC Bank A/C *1643
To MARVIN CLEMENT
On 28-03
Ref 408856589439
Not You? Call 18002586161/SMS BLOCK UPI to 7308080808""",
            """HDFC Bank: Rs. 90.00 credited to a/c XXXXXX1643 on 28-03-24 by a/c linked to VPA clementmarvin05-1@okicici (UPI Ref No  408837405314).""",
            """HDFC Bank: Rs. 3000.00 credited to a/c XXXXXX1643 on 28-03-24 by a/c linked to VPA clementmarvin05-1@okicici (UPI Ref No  408824098233).""",
            """Amt Sent Rs.20k
From HDFC Bank A/C *1643
To MARVIN CLEMENT
On 03-04-20
Ref 408856589439
Not You? Call 18002586161/SMS BLOCK UPI to 7308080808"""
        )
    }

    @After
    fun teardown() {
        messageList = emptyList()
    }

    @Test
    fun testValidOriginAddress_returnsTrue() {
        val originAddress = "AX-HDFCBX"
        val result = extractor.isOriginValidOrg(originAddress)
        assertThat(result).isTrue()
    }

    @Test
    fun testInvalidOriginAddress_returnsFalse() {
        val originAddress = "9846631098"
        val result = extractor.isOriginValidOrg(originAddress)
        assertThat(result).isFalse()
    }

    @Test
    fun testSupportedLanguage_returnsTrue() {
        val string = "Hello"
        val supportedLanguage = extractor.isSupportedLanguage(string)
        assertThat(supportedLanguage).isTrue()
    }

    @Test
    fun testNonSupportedLanguage_returnsFalse() {
        val string = "ഹലോ"
        val supportedLanguage = extractor.isSupportedLanguage(string)
        assertThat(supportedLanguage).isFalse()
    }

    @Test
    fun testValidLanguage_returnsFalse() {
        messageList.forEachIndexed { index, message ->
            println("--- Message No. $index ---")
            println("Content - $message")
            val isSupported = extractor.isSupportedLanguage(message)
            assertThat(isSupported).isTrue()
        }
    }

    @Test
    fun testExtractDataAgainstMultipleMessages_testPasses() {
        messageList.forEachIndexed { index, message ->
            println("--- Message No. $index ---")
            val data = extractor.extractData(message)
            println("Extracted Data - $data")
        }
    }
}