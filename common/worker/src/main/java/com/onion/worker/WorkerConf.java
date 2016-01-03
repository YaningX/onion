package com.onion.worker;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhonglin on 16-1-3.
 */
public class WorkerConf {
    private SAXReader reader;
    private Document document;
    private Element root;
    public WorkerConf(File confDir) {
        reader = new SAXReader();
        try {
            document = reader.read(confDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        root = document.getRootElement();
    }

    public List<String> getBackendDirs() {
        List<String> list = new ArrayList<String>();
        List<Element> elements = root.elements();
        for (Element element : elements) {
            list.add(element.getText());
        }
        return list;
    }
}
