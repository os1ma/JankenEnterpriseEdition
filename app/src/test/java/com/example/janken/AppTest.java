package com.example.janken;

import com.example.janken.domain.dao.JankenDao;
import com.example.janken.domain.dao.JankenDetailDao;
import com.example.janken.framework.TransactionManager;
import com.example.janken.infrastructure.jdbctransaction.JDBCTransactionManager;
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

class AppTest {

    private static final long PLAYER_1_ID = 1;
    private static final long PLAYER_2_ID = 2;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static InputStream defaultStdin = System.in;
    private static PrintStream defaultStdout = System.out;
    private static StandardInputSnatcher stdinSnatcher;
    private static StandardOutputSnatcher stdoutSnatcher;

    private static TransactionManager tm = new JDBCTransactionManager();
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
                                  int player2ResultValue) {

        // 準備

        long jankensCountBeforeTest;
        long jankenDetailsCountBeforeTest;
        try (val tx = tm.startTransaction()) {

            stdinSnatcher.inputLine(String.valueOf(player1HandValue));
            stdinSnatcher.inputLine(String.valueOf(player2HandValue));

            jankensCountBeforeTest = jankenDao.count(tx);
            jankenDetailsCountBeforeTest = jankenDetailDao.count(tx);
        }

        // 実行

        val args = new String[0];
        App.main(args);

        // 検証

        try (val tx = tm.startTransaction()) {

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

            // じゃんけんデータの検証
            assertEquals(jankensCountBeforeTest + 1, jankenDao.count(tx), "じゃんけんが 1 件追加されたこと");

            val savedJankens = jankenDao.findAllOrderById(tx);
            val savedJanken = savedJankens.get(savedJankens.size() - 1);
            val savedJankenId = savedJanken.getId();

            // じゃんけん明細データの検証
            assertEquals(jankenDetailsCountBeforeTest + 2, jankenDetailDao.count(tx),
                    "じゃんけん明細が 2 件追加されたこと");

            // 自動採番される ID 以外の値を検証
            val savedJankenDetails = jankenDetailDao.findAllOrderById(tx);

            {
                val savedJankenDetails1 = savedJankenDetails.get(savedJankenDetails.size() - 2);
                assertEquals(savedJankenId, savedJankenDetails1.getJankenId());
                assertEquals(PLAYER_1_ID, savedJankenDetails1.getPlayerId());
                assertEquals(player1HandValue, savedJankenDetails1.getHand().getValue());
                assertEquals(player1ResultValue, savedJankenDetails1.getResult().getValue());
            }

            {
                val savedJankenDetails2 = savedJankenDetails.get(savedJankenDetails.size() - 1);
                assertEquals(savedJankenId, savedJankenDetails2.getJankenId());
                assertEquals(PLAYER_2_ID, savedJankenDetails2.getPlayerId());
                assertEquals(player2HandValue, savedJankenDetails2.getHand().getValue());
                assertEquals(player2ResultValue, savedJankenDetails2.getResult().getValue());
            }

        }
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
    void 不正な入力で再入力が促される(String invalidInput) {

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
