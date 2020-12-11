package com.example.janken.infrastructure.csvdao;

import com.example.janken.domain.dao.JankenDao;
import com.example.janken.domain.model.Janken;
import com.example.janken.domain.model.JankenDetail;
import com.example.janken.domain.transaction.Transaction;
import lombok.val;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JankenCsvDao implements JankenDao {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static final String JANKENS_CSV = CsvDaoUtils.DATA_DIR + "jankens.csv";

    @Override
    public List<Janken> findAllOrderById(Transaction tx) {
        try (val stream = Files.lines(Paths.get(JANKENS_CSV), StandardCharsets.UTF_8)) {
            return stream.map(this::line2Janken)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<Janken> findById(Transaction tx, long id) {
        try (val stream = Files.lines(Paths.get(JANKENS_CSV), StandardCharsets.UTF_8)) {
            return stream.map(this::line2Janken)
                    .filter(j -> j.getId() == id)
                    .findFirst();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public long count(Transaction tx) {
        return CsvDaoUtils.countFileLines(JANKENS_CSV);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Janken insert(Transaction tx, Janken janken) {
        val jankensCsv = new File(JANKENS_CSV);

        try (val fw = new FileWriter(jankensCsv, true);
             val bw = new BufferedWriter(fw);
             val pw = new PrintWriter(bw)) {

            // ファイルが存在しない場合に備えて作成
            jankensCsv.createNewFile();

            val jankenId = CsvDaoUtils.countFileLines(JANKENS_CSV) + 1;

            val detail1 = janken.getDetail1();
            val detail1WithId = new JankenDetail(null, jankenId, detail1.getPlayerId(), detail1.getHand(), detail1.getResult());
            val detail2 = janken.getDetail2();
            val detail2WithId = new JankenDetail(null, jankenId, detail2.getPlayerId(), detail2.getHand(), detail2.getResult());
            val jankenWithId = new Janken(jankenId, janken.getPlayedAt(), detail1WithId, detail2WithId);

            val line = janken2Line(jankenWithId);
            pw.println(line);

            return jankenWithId;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Janken line2Janken(String line) {
        val values = line.split(CsvDaoUtils.CSV_DELIMITER);
        val jankenId = Long.valueOf(values[0]);
        val playedAt = LocalDateTime.parse(values[1], DATE_TIME_FORMATTER);

        return new Janken(jankenId, playedAt, null, null);
    }

    private String janken2Line(Janken janken) {
        val playedAtStr = DATE_TIME_FORMATTER.format(janken.getPlayedAt());

        return janken.getId() + CsvDaoUtils.CSV_DELIMITER + playedAtStr;
    }

}
