package com.example.janken.presentation.cli.view;

import lombok.val;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class View {

    static {
        Velocity.setProperty("resource.loader", "class");
        Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.setProperty("input.encoding", "UTF-8");
        Velocity.setProperty("output.encoding", "UTF-8");
        Velocity.init();
    }

    private String templateName;
    private Map<String, Object> map;

    private View(String templateName, Map<String, Object> map) {
        this.templateName = templateName;
        this.map = map;
    }

    public View(String templateName) {
        this.templateName = templateName;
        this.map = new HashMap<>();
    }

    public View with(String key, Object value) {
        val newMap = new HashMap<String, Object>(map);
        newMap.put(key, value);
        return new View(templateName, newMap);
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
