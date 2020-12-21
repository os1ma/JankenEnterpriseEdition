package com.example.janken.presentation.cli.view;

import com.example.janken.presentation.cli.controller.CLIController;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StandardOutputView {

    static {
        Velocity.setProperty("resource.loader", "class");
        Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.setProperty("input.encoding", "UTF-8");
        Velocity.setProperty("output.encoding", "UTF-8");
        Velocity.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogSystem");
        Velocity.init();
    }

    private static final Scanner STDIN_SCANNER = new Scanner(System.in);

    private String templateName;
    private Map<String, Object> map;
    private CLIController next;

    public StandardOutputView(String templateName) {
        this.templateName = templateName;
        this.map = new HashMap<>();
    }

    public StandardOutputView with(String key, Object value) {
        val newMap = new HashMap<>(map);
        newMap.put(key, value);
        return new StandardOutputView(templateName, newMap, next);
    }

    public StandardOutputView next(CLIController next) {
        return new StandardOutputView(templateName, map, next);
    }

    public void show() {
        try (val sw = new StringWriter()) {

            val vc = new VelocityContext();
            map.forEach(vc::put);

            val template = Velocity.getTemplate(templateName);
            template.merge(vc, sw);

            System.out.print(sw.toString());

            // 次がある場合は進む
            if (next != null) {
                val input = STDIN_SCANNER.nextLine();
                next.handle(input);
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
