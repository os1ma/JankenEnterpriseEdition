package com.example.janken.infrastructure.csvdao;

import com.example.janken.domain.dao.JankenDetailDao;
import com.example.janken.domain.model.Hand;
import com.example.janken.domain.model.JankenDetail;
import com.example.janken.domain.model.Result;
import com.example.janken.framework.Transaction;
import lombok.val;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JankenDetailCsvDao implements JankenDetailDao {

    private static final String JANKEN_DETAILS_CSV = CsvDaoUtils.DATA_DIR + "janken_details.csv";

    @Override
    public List<JankenDetail> findAllOrderById(Transaction tx) {
        try (val stream = Files.lines(Paths.get(JANKEN_DETAILS_CSV), StandardCharsets.UTF_8)) {
            return stream.map(this::line2JankenDetail)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<JankenDetail> findById(Transaction tx, long id) {
        try (val stream = Files.lines(Paths.get(JANKEN_DETAILS_CSV), StandardCharsets.UTF_8)) {
            return stream.map(this::line2JankenDetail)
                    .filter(j -> j.getId() == id)
                    .findFirst();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public long count(Transaction tx) {
        return CsvDaoUtils.countFileLines(JANKEN_DETAILS_CSV);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public List<JankenDetail> insertAll(Transaction tx, List<JankenDetail> jankenDetails) {
        val jankenDetailsCsv = new File(JANKEN_DETAILS_CSV);

        try (val fw = new FileWriter(jankenDetailsCsv, true);
             val bw = new BufferedWriter(fw);
             val pw = new PrintWriter(bw)) {

            // ファイルが存在しない場合に備えて作成
            jankenDetailsCsv.createNewFile();

            val jankenDetailWithIds = new ArrayList<JankenDetail>();
            for (int i = 0; i < jankenDetails.size(); i++) {
                val jankenDetail = jankenDetails.get(i);

                val jankenDetailId = CsvDaoUtils.countFileLines(JANKEN_DETAILS_CSV) + i + 1;
                val jankenDetailWithId = new JankenDetail(
                        jankenDetailId,
                        jankenDetail.getJankenId(),
                        jankenDetail.getPlayerId(),
                        jankenDetail.getHand(),
                        jankenDetail.getResult());

                val line = jankenDetail2Line(jankenDetailWithId);
                pw.println(line);

                jankenDetailWithIds.add(jankenDetailWithId);
            }

            return jankenDetailWithIds;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private JankenDetail line2JankenDetail(String line) {
        val values = line.split(CsvDaoUtils.CSV_DELIMITER);
        val jankenDetailId = Long.valueOf(values[0]);
        val jankenId = Long.valueOf(values[1]);
        val playerId = Long.valueOf(values[2]);
        val hand = Hand.of(Integer.parseInt(values[3]));
        val result = Result.of(Integer.parseInt(values[4]));

        return new JankenDetail(
                jankenDetailId,
                jankenId,
                playerId,
                hand,
                result);
    }

    private String jankenDetail2Line(JankenDetail jankenDetail) {
        return String.join(CsvDaoUtils.CSV_DELIMITER,
                String.valueOf(jankenDetail.getId()),
                String.valueOf(jankenDetail.getJankenId()),
                String.valueOf(jankenDetail.getPlayerId()),
                String.valueOf(jankenDetail.getHand().getValue()),
                String.valueOf(jankenDetail.getResult().getValue()));
    }

}
