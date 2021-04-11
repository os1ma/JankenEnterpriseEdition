package com.example.janken.presentation.cli.view;

import lombok.val;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.helpers.NOPLogger;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class StandardOutputView {

    static {
        Velocity.setProperty("runtime.log.instance", NOPLogger.NOP_LOGGER);
        Velocity.setProperty("resource.loaders", "class");
        Velocity.setProperty("resource.loader.class.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.setProperty("resource.default_encoding", "UTF-8");
        Velocity.init();
    }

    private String templateName;
    private Map<String, Object> map;

    private StandardOutputView(String templateName, Map<String, Object> map) {
        this.templateName = templateName;
        this.map = map;
    }

    public StandardOutputView(String templateName) {
        this.templateName = templateName;
        this.map = new HashMap<>();
    }

    public StandardOutputView with(String key, Object value) {
        val newMap = new HashMap<String, Object>(map);
        newMap.put(key, value);
        return new StandardOutputView(templateName, newMap);
    }

    public void show() {
        try (val sw = new StringWriter()) {

            val vc = new VelocityContext();
            map.forEach(vc::put);

            val template = Velocity.getTemplate(templateName);
            template.merge(vc, sw);

            System.out.print(sw.toString());

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
