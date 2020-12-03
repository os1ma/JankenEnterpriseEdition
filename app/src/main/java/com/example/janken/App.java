package com.example.janken;

import com.example.janken.model.Hand;
import com.example.janken.model.Result;
import lombok.val;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

public class App {

    // ID は実際のアプリケーションでは認証情報から取得することが想定される
    private static final int PLAYER_1_ID = 1;
    private static final int PLAYER_2_ID = 2;

    // 表示するメッセージの形式定義

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String SCAN_PROMPT_MESSAGE_FORMAT = String.join(LINE_SEPARATOR,
            Hand.STONE.getName() + ": " + Hand.STONE.getValue(),
            Hand.PAPER.getName() + ": " + Hand.PAPER.getValue(),
            Hand.SCISSORS.getName() + ": " + Hand.SCISSORS.getValue(),
            "Please select {0} hand:");
    private static final String INVALID_INPUT_MESSAGE_FORMAT = "Invalid input: {0}" + LINE_SEPARATOR;
    private static final String SHOW_HAND_MESSAGE_FORMAT = "{0} selected {1}";
    private static final String WINNING_MESSAGE_FORMAT = "{0} win !!!";
    private static final String DRAW_MESSAGE = "DRAW !!!";

    // 入力スキャナ

    private static final Scanner STDIN_SCANNER = new Scanner(System.in);

    // データ保存に関する定義

    // JankenEnterpriseEdition/app/../data/ を指す
    private static final String DEFAULT_DATA_DIR = System.getProperty("user.dir") + "/../data/";
    private static final String DATA_DIR_ENV_VARIABLE = System.getenv("DATA_DIR");
    private static final String DATA_DIR = DATA_DIR_ENV_VARIABLE != null ? DATA_DIR_ENV_VARIABLE + "/" : DEFAULT_DATA_DIR;
    private static final String PLAYERS_CSV = DATA_DIR + "players.csv";
    private static final String JANKENS_CSV = DATA_DIR + "jankens.csv";
    private static final String JANKEN_DETAILS_CSV = DATA_DIR + "janken_details.csv";
    private static final String CSV_DELIMITER = ",";

    public static void main(String[] args) throws IOException {

        // プレイヤー名を取得

        String player1Name = findPlayerNameById(PLAYER_1_ID);
        String player2Name = findPlayerNameById(PLAYER_2_ID);

        // プレイヤーの手を取得

        Hand player1Hand = scanHand(player1Name);
        Hand player2Hand = scanHand(player2Name);

        showHandWithName(player1Hand, player1Name);
        showHandWithName(player2Hand, player2Name);

        // 勝敗判定

        Result player1Result;
        Result player2Result;
        if (player1Hand.equals(Hand.STONE)) {
            // プレイヤーがグーの場合

            if (player2Hand.equals(Hand.STONE)) {
                player1Result = Result.DRAW;
                player2Result = Result.DRAW;
            } else if (player2Hand.equals(Hand.PAPER)) {
                player1Result = Result.LOSE;
                player2Result = Result.WIN;
            } else {
                player1Result = Result.WIN;
                player2Result = Result.LOSE;
            }

        } else if (player1Hand.equals(Hand.PAPER)) {
            // プレイヤーがパーの場合

            if (player2Hand.equals(Hand.STONE)) {
                player1Result = Result.WIN;
                player2Result = Result.LOSE;
            } else if (player2Hand.equals(Hand.PAPER)) {
                player1Result = Result.DRAW;
                player2Result = Result.DRAW;
            } else {
                player1Result = Result.LOSE;
                player2Result = Result.WIN;
            }

        } else {
            // プレイヤーがチョキの場合

            if (player2Hand.equals(Hand.STONE)) {
                player1Result = Result.LOSE;
                player2Result = Result.WIN;
            } else if (player2Hand.equals(Hand.PAPER)) {
                player1Result = Result.WIN;
                player2Result = Result.LOSE;
            } else {
                player1Result = Result.DRAW;
                player2Result = Result.DRAW;
            }
        }

        // 結果を保存

        File jankensCsv = new File(JANKENS_CSV);
        jankensCsv.createNewFile();
        long jankenId = countFileLines(JANKENS_CSV) + 1;
        LocalDateTime playedAt = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss");
        String playedAtStr = formatter.format(playedAt);
        try (FileWriter fw = new FileWriter(jankensCsv, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println(jankenId + CSV_DELIMITER + playedAtStr);
        }

        File jankenDetailsCsv = new File(JANKEN_DETAILS_CSV);
        jankenDetailsCsv.createNewFile();
        long jankenDetailsCount = countFileLines(JANKEN_DETAILS_CSV);
        try (FileWriter fw = new FileWriter(jankenDetailsCsv, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {

            long jankenDetail1Id = jankenDetailsCount + 1;
            writeJankenDetail(pw, jankenDetail1Id, jankenId, PLAYER_1_ID, player1Hand, player1Result);
            long jankenDetail2Id = jankenDetailsCount + 2;
            writeJankenDetail(pw, jankenDetail2Id, jankenId, PLAYER_2_ID, player2Hand, player2Result);
        }

        // 勝敗の表示

        String resultMessage;
        if (player1Result.equals(Result.WIN)) {
            resultMessage = MessageFormat.format(WINNING_MESSAGE_FORMAT, player1Name);
        } else if (player2Result.equals(Result.WIN)) {
            resultMessage = MessageFormat.format(WINNING_MESSAGE_FORMAT, player2Name);
        } else {
            resultMessage = DRAW_MESSAGE;
        }

        System.out.println(resultMessage);
    }

    private static String findPlayerNameById(int playerId) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(PLAYERS_CSV), StandardCharsets.UTF_8)) {
            return stream
                    // ID で検索
                    .filter(line -> {
                        String[] values = line.split(CSV_DELIMITER);
                        int id = Integer.parseInt(values[0]);
                        return id == playerId;
                    })
                    // 名前のみに変換
                    .map(line -> {
                        String[] values = line.split(CSV_DELIMITER);
                        return values[1];
                    })
                    .findFirst()
                    .orElseThrow(() -> {
                        throw new IllegalArgumentException("Player not exist. playerId = " + playerId);
                    });
        }
    }

    private static long countFileLines(String path) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            return stream.count();
        }
    }

    private static Hand scanHand(String playerName) {
        while (true) {
            System.out.println(MessageFormat.format(SCAN_PROMPT_MESSAGE_FORMAT, playerName));
            String inputStr = STDIN_SCANNER.nextLine();

            val maybeHand = Arrays.stream(Hand.values())
                    .filter(hand -> {
                        val handValueStr = String.valueOf(hand.getValue());
                        return handValueStr.equals(inputStr);
                    })
                    .findFirst();

            if (maybeHand.isPresent()) {
                return maybeHand.get();
            } else {
                System.out.println(MessageFormat.format(INVALID_INPUT_MESSAGE_FORMAT, inputStr));
            }
        }
    }

    private static void showHandWithName(Hand hand, String name) {
        System.out.println(MessageFormat.format(SHOW_HAND_MESSAGE_FORMAT, name, hand.getName()));
    }

    private static void writeJankenDetail(PrintWriter pw,
                                          long jankenDetailId,
                                          long jankenId,
                                          int playerId,
                                          Hand playerHand,
                                          Result playerResult) {
        String line = String.join(CSV_DELIMITER,
                String.valueOf(jankenDetailId),
                String.valueOf(jankenId),
                String.valueOf(playerId),
                String.valueOf(playerHand.getValue()),
                String.valueOf(playerResult.getValue()));
        pw.println(line);
    }

}