package com.onion.master;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by sunhonglin on 16-3-2.
 */
public class FileConf {
    private String filename;
    private long blockSize;
    private long fileSize;
    private long blockId;

    private File confDir;
    private SAXReader reader = new SAXReader();
    private Document document;
    private Element root;

    public FileConf(File confDir) {
        this.confDir = confDir;
        reader = new SAXReader();
        try {
            document = reader.read(confDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        root = document.getRootElement();
    }

    public String getFilename() {
        return root.element("name").getText();
    }

    public long getBlockSize() {
        return Long.parseLong(root.element("blockSize").getText());
    }

    public long getFileSize() {
        return Long.parseLong(root.element("fileSize").getText());
    }

    public long getBlockId() {
        return Long.parseLong(root.element("blockId").getText());
    }

    public void setFileInfo(String filename, long blockSize, long fileSize, long blockId)
            throws Exception {
        root.element("name").setText(filename);
        root.element("blockSize").setText(String.valueOf(blockSize));
        root.element("fileSize").setText(String.valueOf(fileSize));
        root.element("blockId").setText(String.valueOf(blockId));
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(confDir), format);
        writer.write(document);
    }
}
