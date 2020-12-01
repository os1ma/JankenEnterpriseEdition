package com.example.janken;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String DATA_DIR = System.getProperty("user.dir") + "/../data/";
    private static final String JANKENS_CSV = DATA_DIR + "jankens.csv";
    private static final String JANKEN_DETAILS_CSV = DATA_DIR + "janken_details.csv";

    private static InputStream defaultStdin = System.in;
    private static PrintStream defaultStdout = System.out;
    private static StandardInputSnatcher stdinSnatcher;
    private static StandardOutputSnatcher stdoutSnatcher;

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
    void 正常な入力でじゃんけんが実行され結果が保存される(int player1HandNum,
                                  int player2HandNum,
                                  String player1HandName,
                                  String player2HandName,
                                  String resultMessage,
                                  int player1Result,
                                  int player2Result) throws IOException {

        // 準備

        stdinSnatcher.inputLine(String.valueOf(player1HandNum));
        stdinSnatcher.inputLine(String.valueOf(player2HandNum));

        File jankensCsv = new File(JANKENS_CSV);
        jankensCsv.createNewFile();
        var jankensCsvLengthBeforeTest = countFileLines(JANKENS_CSV);

        File jankenDetailsCsv = new File(JANKEN_DETAILS_CSV);
        jankenDetailsCsv.createNewFile();
        var jankenDetailsCsvLengthBeforeTest = countFileLines(JANKEN_DETAILS_CSV);

        // 実行

        String[] args = new String[0];
        App.main(args);

        // 検証

        // 標準出力の検証
        var actualStdout = stdoutSnatcher.readAllLines();
        var expectedStdout = String.join(LINE_SEPARATOR, Arrays.asList(
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
        var appendedJankenId = jankensCsvLengthBeforeTest + 1;
        assertEquals(appendedJankenId, countFileLines(JANKENS_CSV),
                "じゃんけんデータの CSV に 1 行追加されたこと");
        var jankenCsvLine = readSpecifiedLineByFile(JANKENS_CSV, jankensCsvLengthBeforeTest + 1);
        assertTrue(jankenCsvLine.matches(appendedJankenId + ",\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}"),
                "じゃんけんデータの CSV に追加された内容が想定通りであること");

        // じゃんけん明細データの CSV の検証
        assertEquals(jankenDetailsCsvLengthBeforeTest + 2, countFileLines(JANKEN_DETAILS_CSV),
                "じゃんけん明細データの CSV に 2 行追加されたこと");
        var jankenDetailsCsvAppendedLine1 = (jankenDetailsCsvLengthBeforeTest + 1) + "," + appendedJankenId + ",1," + player1HandNum + "," + player1Result;
        assertEquals(jankenDetailsCsvAppendedLine1,
                readSpecifiedLineByFile(JANKEN_DETAILS_CSV, jankenDetailsCsvLengthBeforeTest + 1),
                "じゃんけん明細データの CSV に追加された 1 行目の内容が想定通りであること");
        var jankenDetailsCsvAppendedLine2 = (jankenDetailsCsvLengthBeforeTest + 2) + "," + appendedJankenId + ",2," + player2HandNum + "," + player2Result;
        assertEquals(jankenDetailsCsvAppendedLine2,
                readSpecifiedLineByFile(JANKEN_DETAILS_CSV, jankenDetailsCsvLengthBeforeTest + 2),
                "じゃんけん明細データの CSV に追加された 2 行目の内容が想定通りであること");
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

        String validInput1 = "0";
        String validInput2 = "0";
        stdinSnatcher.inputLine(validInput1);
        stdinSnatcher.inputLine(validInput2);
        String[] args = new String[0];
        App.main(args);

        var actual = stdoutSnatcher.readAllLines();
        var expected = String.join(LINE_SEPARATOR, Arrays.asList(
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

    private static long countFileLines(String path) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            return stream.count();
        }
    }

    private static String readSpecifiedLineByFile(String path, long index) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            return stream.limit(index)
                    .skip(index - 1)
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
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
        int result = sb.charAt(0);
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
        StringBuilder sb = new StringBuilder();

        boolean isFirstLine = true;
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
            String line = "";
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
