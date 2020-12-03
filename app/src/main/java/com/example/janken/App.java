package com.example.janken;

import com.example.janken.framework.View;
import com.example.janken.model.*;
import lombok.val;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;

public class App {

    // ID は実際のアプリケーションでは認証情報から取得することが想定される
    private static final long PLAYER_1_ID = 1;
    private static final long PLAYER_2_ID = 2;

    // 入力スキャナ
    private static final Scanner STDIN_SCANNER = new Scanner(System.in);

    private static final String VIEW_RESOURCE_PREFIX = "view/";

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

        val player1 = findPlayerById(PLAYER_1_ID);
        val player2 = findPlayerById(PLAYER_2_ID);

        // プレイヤーの手を取得

        val player1Hand = scanHand(player1);
        val player2Hand = scanHand(player2);

        showHandWithName(player1Hand, player1);
        showHandWithName(player2Hand, player2);

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

        // じゃんけんを生成

        val jankensCsv = new File(JANKENS_CSV);
        jankensCsv.createNewFile();

        val jankenId = countFileLines(JANKENS_CSV) + 1;
        val playedAt = LocalDateTime.now();
        val janken = new Janken(jankenId, playedAt);

        // じゃんけんを保存

        try (val fw = new FileWriter(jankensCsv, true);
             val bw = new BufferedWriter(fw);
             val pw = new PrintWriter(bw)) {

            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss");
            val playedAtStr = formatter.format(janken.getPlayedAt());
            pw.println(janken.getId() + CSV_DELIMITER + playedAtStr);
        }

        // じゃんけん明細を生成

        val jankenDetailsCsv = new File(JANKEN_DETAILS_CSV);
        jankenDetailsCsv.createNewFile();
        val jankenDetailsCount = countFileLines(JANKEN_DETAILS_CSV);

        val jankenDetail1Id = jankenDetailsCount + 1;
        val jankenDetail1 = new JankenDetail(jankenDetail1Id, jankenId, PLAYER_1_ID, player1Hand, player1Result);

        val jankenDetail2Id = jankenDetailsCount + 2;
        val jankenDetail2 = new JankenDetail(jankenDetail2Id, jankenId, PLAYER_2_ID, player2Hand, player2Result);

        // じゃんけん明細を保存

        try (val fw = new FileWriter(jankenDetailsCsv, true);
             val bw = new BufferedWriter(fw);
             val pw = new PrintWriter(bw)) {

            writeJankenDetail(pw, jankenDetail1);
            writeJankenDetail(pw, jankenDetail2);
        }

        // 勝敗の表示

        Player winner = null;
        if (player1Result.equals(Result.WIN)) {
            winner = player1;
        } else if (player2Result.equals(Result.WIN)) {
            winner = player2;
        }

        new View(VIEW_RESOURCE_PREFIX + "result.vm")
                .with("winner", winner)
                .show();
    }

    private static Player findPlayerById(long playerId) throws IOException {
        try (val stream = Files.lines(Paths.get(PLAYERS_CSV), StandardCharsets.UTF_8)) {
            return stream
                    .map(line -> {
                        val values = line.split(CSV_DELIMITER);
                        val id = Long.parseLong(values[0]);
                        val name = values[1];
                        return new Player(id, name);
                    })
                    // ID で検索
                    .filter(p -> p.getId() == playerId)
                    .findFirst()
                    .orElseThrow(() -> {
                        throw new IllegalArgumentException("Player not exist. playerId = " + playerId);
                    });
        }
    }

    private static long countFileLines(String path) throws IOException {
        try (val stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            return stream.count();
        }
    }

    private static Hand scanHand(Player player) {
        while (true) {
            new View(VIEW_RESOURCE_PREFIX + "scan-prompt.vm")
                    .with("player", player)
                    .with("hands", Hand.values())
                    .show();

            val inputStr = STDIN_SCANNER.nextLine();

            val maybeHand = Arrays.stream(Hand.values())
                    .filter(hand -> {
                        val handValueStr = String.valueOf(hand.getValue());
                        return handValueStr.equals(inputStr);
                    })
                    .findFirst();

            if (maybeHand.isPresent()) {
                return maybeHand.get();
            } else {
                new View(VIEW_RESOURCE_PREFIX + "invalid-input.vm")
                        .with("input", inputStr)
                        .show();
            }
        }
    }

    private static void showHandWithName(Hand hand, Player player) {
        new View(VIEW_RESOURCE_PREFIX + "show-hand.vm")
                .with("player", player)
                .with("hand", hand)
                .show();
    }

    private static void writeJankenDetail(PrintWriter pw,
                                          JankenDetail jankenDetail) {
        val line = String.join(CSV_DELIMITER,
                String.valueOf(jankenDetail.getId()),
                String.valueOf(jankenDetail.getJankenId()),
                String.valueOf(jankenDetail.getPlayerId()),
                String.valueOf(jankenDetail.getHand().getValue()),
                String.valueOf(jankenDetail.getResult().getValue()));
        pw.println(line);
    }

}