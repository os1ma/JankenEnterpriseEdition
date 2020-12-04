package com.example.janken.dao;

import com.example.janken.model.JankenDetail;
import lombok.val;

import java.io.*;

public class JankenDetailDao {

    private static final String JANKEN_DETAILS_CSV = DaoUtils.DATA_DIR + "janken_details.csv";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public JankenDetail insert(JankenDetail jankenDetail) {
        val jankenDetailsCsv = new File(JANKEN_DETAILS_CSV);

        try (val fw = new FileWriter(jankenDetailsCsv, true);
             val bw = new BufferedWriter(fw);
             val pw = new PrintWriter(bw)) {

            // ファイルが存在しない場合に備えて作成
            jankenDetailsCsv.createNewFile();

            val jankenDetailId = DaoUtils.countFileLines(JANKEN_DETAILS_CSV) + 1;
            val jankenDetailWithId = new JankenDetail(
                    jankenDetailId,
                    jankenDetail.getJankenId(),
                    jankenDetail.getPlayerId(),
                    jankenDetail.getHand(),
                    jankenDetail.getResult());

            writeJankenDetail(pw, jankenDetailWithId);

            return jankenDetailWithId;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeJankenDetail(PrintWriter pw,
                                          JankenDetail jankenDetail) {
        val line = String.join(DaoUtils.CSV_DELIMITER,
                String.valueOf(jankenDetail.getId()),
                String.valueOf(jankenDetail.getJankenId()),
                String.valueOf(jankenDetail.getPlayerId()),
                String.valueOf(jankenDetail.getHand().getValue()),
                String.valueOf(jankenDetail.getResult().getValue()));
        pw.println(line);
    }

}
