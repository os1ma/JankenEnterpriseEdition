package com.example.janken;

import com.example.janken.domain.dao.JankenDao;
import com.example.janken.domain.dao.JankenDetailDao;
import com.example.janken.domain.model.Hand;
import com.example.janken.domain.model.JankenDetail;
import com.example.janken.domain.model.Result;
import com.example.janken.infrastructure.mysqldao.JankenDetailMySQLDao;
import com.example.janken.infrastructure.mysqldao.JankenMySQLDao;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static InputStream defaultStdin = System.in;
    private static PrintStream defaultStdout = System.out;
    private static StandardInputSnatcher stdinSnatcher;
    private static StandardOutputSnatcher stdoutSnatcher;

    private static JankenDao jankenDao = new JankenMySQLDao();
    private static JankenDetailDao jankenDetailDao = new JankenDetailMySQLDao();

    @BeforeAll
    static void setup() {
        stdinSnatcher = new StandardInputSnatcher();
        System.setIn(stdinSnatcher);
        stdoutSnatcher = new StandardOutputSnatcher();
        System.setOut(stdoutSnatcher);
    }

    @AfterAll
    static void tearDown() {
        System.setIn(defaultStdin);
        System.setOut(defaultStdout);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, STONE, STONE, DRAW !!!, 2, 2",
            "0, 1, STONE, PAPER, Bob win !!!, 1, 0",
            "0, 2, STONE, SCISSORS, Alice win !!!, 0, 1",
            "1, 0, PAPER, STONE, Alice win !!!, 0, 1",
            "1, 1, PAPER, PAPER, DRAW !!!, 2, 2",
            "1, 2, PAPER, SCISSORS, Bob win !!!, 1, 0",
            "2, 0, SCISSORS, STONE, Bob win !!!, 1, 0",
            "2, 1, SCISSORS, PAPER, Alice win !!!, 0, 1",
            "2, 2, SCISSORS, SCISSORS, DRAW !!!, 2, 2"
    })
    void 正常な入力でじゃんけんが実行され結果が保存される(int player1HandValue,
                                  int player2HandValue,
                                  String player1HandName,
                                  String player2HandName,
                                  String resultMessage,
                                  int player1ResultValue,
                                  int player2ResultValue) throws IOException {

        // 準備

        stdinSnatcher.inputLine(String.valueOf(player1HandValue));
        stdinSnatcher.inputLine(String.valueOf(player2HandValue));

        val jankensCountBeforeTest = jankenDao.count();
        val jankenDetailsCountBeforeTest = jankenDetailDao.count();

        // 実行

        val args = new String[0];
        App.main(args);

        // 検証

        // 標準出力の検証
        val actualStdout = stdoutSnatcher.readAllLines();
        val expectedStdout = String.join(LINE_SEPARATOR, Arrays.asList(
                "STONE: 0",
                "PAPER: 1",
                "SCISSORS: 2",
                "Please select Alice hand:",
                "STONE: 0",
                "PAPER: 1",
                "SCISSORS: 2",
                "Please select Bob hand:",
                "Alice selected " + player1HandName,
                "Bob selected " + player2HandName,
                resultMessage
        ));
        assertEquals(expectedStdout, actualStdout, "標準出力の内容が想定通りであること");

        // じゃんけんデータの CSV の検証
        assertEquals(jankensCountBeforeTest + 1, jankenDao.count(), "じゃんけんが 1 件追加されたこと");
        val expectedJankenId = jankensCountBeforeTest + 1;
        val savedJanken = jankenDao.findById(jankensCountBeforeTest + 1);
        assertTrue(savedJanken.isPresent(), "じゃんけんが保存されていること");

        // じゃんけん明細データの CSV の検証
        assertEquals(jankenDetailsCountBeforeTest + 2, jankenDetailDao.count(),
                "じゃんけん明細が 2 行件追加されたこと");

        val expectedJankenDetail1Id = jankenDetailsCountBeforeTest + 1;
        val expectedJankenDetail1 = new JankenDetail(
                expectedJankenDetail1Id,
                expectedJankenId,
                1L,
                Hand.of(player1HandValue),
                Result.of(player1ResultValue));
        val savedJankenDetail1 = jankenDetailDao.findById(expectedJankenDetail1Id);
        assertEquals(expectedJankenDetail1, savedJankenDetail1.get(),
                "じゃんけん明細に追加された 1 件目の内容が想定通りであること");

        val expectedJankenDetail2Id = jankenDetailsCountBeforeTest + 2;
        val expectedJankenDetail2 = new JankenDetail(
                expectedJankenDetail2Id,
                expectedJankenId,
                2L,
                Hand.of(player2HandValue),
                Result.of(player2ResultValue));
        val savedJankenDetail2 = jankenDetailDao.findById(expectedJankenDetail2Id);
        assertEquals(expectedJankenDetail2, savedJankenDetail2.get(),
                "じゃんけん明細に追加された 2 件目の内容が想定通りであること");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-1",
            "3",
            "1.0",
            "a",
            "",
            " "
    })
    void 不正な入力で再入力が促される(String invalidInput) throws IOException {

        stdinSnatcher.inputLine(String.valueOf(invalidInput));

        val validInput1 = "0";
        val validInput2 = "0";
        stdinSnatcher.inputLine(validInput1);
        stdinSnatcher.inputLine(validInput2);
        val args = new String[0];
        App.main(args);

        val actual = stdoutSnatcher.readAllLines();
        val expected = String.join(LINE_SEPARATOR, Arrays.asList(
                "STONE: 0",
                "PAPER: 1",
                "SCISSORS: 2",
                "Please select Alice hand:",
                "Invalid input: " + invalidInput,
                "",
                "STONE: 0",
                "PAPER: 1",
                "SCISSORS: 2",
                "Please select Alice hand:",
                "STONE: 0",
                "PAPER: 1",
                "SCISSORS: 2",
                "Please select Bob hand:",
                "Alice selected STONE",
                "Bob selected STONE",
                "DRAW !!!"
        ));

        assertEquals(expected, actual);
    }

}

class StandardInputSnatcher extends InputStream {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private StringBuilder sb = new StringBuilder();

    public void inputLine(String str) {
        sb.append(str).append(LINE_SEPARATOR);
    }

    @Override
    public int read() {
        if (sb.length() == 0) {
            return -1;
        }
        val result = sb.charAt(0);
        sb.deleteCharAt(0);
        return result;
    }
}

class StandardOutputSnatcher extends PrintStream {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private BufferedReader br = new BufferedReader(new StringReader(""));

    public StandardOutputSnatcher() {
        super(new ByteArrayOutputStream());
    }

    public String readAllLines() {
        val sb = new StringBuilder();

        var isFirstLine = true;
        String line;
        while ((line = readLine()) != null) {
            // 最初の行以外の場合、行の前に改行コードを入れる
            if (isFirstLine) {
                isFirstLine = false;
            } else {
                sb.append(LINE_SEPARATOR);
            }

            sb.append(line);
        }

        return sb.toString();
    }

    private String readLine() {
        try {
            var line = "";
            if ((line = br.readLine()) != null) {
                return line;
            } else {
                br = new BufferedReader(new StringReader(out.toString()));
                ((ByteArrayOutputStream) out).reset();
                return br.readLine();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
