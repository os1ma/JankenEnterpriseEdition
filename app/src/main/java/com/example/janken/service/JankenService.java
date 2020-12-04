package com.example.janken.service;

import com.example.janken.model.*;
import lombok.val;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class JankenService {

    private static final String JANKENS_CSV = ServiceConfigurations.DATA_DIR + "jankens.csv";
    private static final String JANKEN_DETAILS_CSV = ServiceConfigurations.DATA_DIR + "janken_details.csv";

    /**
     * じゃんけんを実行し、勝者を返します。
     */
    public Optional<Player> play(Player player1, Hand player1Hand,
                                 Player player2, Hand player2Hand) throws IOException {

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
            pw.println(janken.getId() + ServiceConfigurations.CSV_DELIMITER + playedAtStr);
        }

        // じゃんけん明細を生成

        val jankenDetailsCsv = new File(JANKEN_DETAILS_CSV);
        jankenDetailsCsv.createNewFile();
        val jankenDetailsCount = countFileLines(JANKEN_DETAILS_CSV);

        val jankenDetail1Id = jankenDetailsCount + 1;
        val jankenDetail1 = new JankenDetail(jankenDetail1Id, jankenId, player1.getId(), player1Hand, player1Result);

        val jankenDetail2Id = jankenDetailsCount + 2;
        val jankenDetail2 = new JankenDetail(jankenDetail2Id, jankenId, player2.getId(), player2Hand, player2Result);

        // じゃんけん明細を保存

        try (val fw = new FileWriter(jankenDetailsCsv, true);
             val bw = new BufferedWriter(fw);
             val pw = new PrintWriter(bw)) {

            writeJankenDetail(pw, jankenDetail1);
            writeJankenDetail(pw, jankenDetail2);
        }

        // 勝者を返却

        if (player1Result.equals(Result.WIN)) {
            return Optional.of(player1);
        } else if (player2Result.equals(Result.WIN)) {
            return Optional.of(player2);
        } else {
            return Optional.empty();
        }
    }

    private static long countFileLines(String path) throws IOException {
        try (val stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            return stream.count();
        }
    }

    private static void writeJankenDetail(PrintWriter pw,
                                          JankenDetail jankenDetail) {
        val line = String.join(ServiceConfigurations.CSV_DELIMITER,
                String.valueOf(jankenDetail.getId()),
                String.valueOf(jankenDetail.getJankenId()),
                String.valueOf(jankenDetail.getPlayerId()),
                String.valueOf(jankenDetail.getHand().getValue()),
                String.valueOf(jankenDetail.getResult().getValue()));
        pw.println(line);
    }

}
