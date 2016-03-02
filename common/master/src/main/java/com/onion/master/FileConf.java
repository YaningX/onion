package com.onion.master;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

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
    private Element file;

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
        return file.element("name").getText();
    }

    public long getBlockSize() {
        return Long.parseLong(file.element("blockSize").getText());
    }

    public long getFileSize() {
        return Long.parseLong(file.element("fileSize").getText());
    }

    public long getBlockId() {
        return Long.parseLong(file.element("blockId").getText());
    }

    public void setFile(long blockId) {
        String id = String.valueOf(blockId);
        List list = document.selectNodes("/files/file/blockId");
        for (Object object : list) {
            Element element = (Element) object;
            if (element.getText().equals(id)) {
                file = element.getParent();
                break;
            }
        }
    }

    public void setFileInfo(String filename, long blockSize, long fileSize, long blockId)
            throws Exception {
        Element file = root.addElement("file");
        file.addElement("name").setText(filename);
        file.addElement("blockSize").setText(String.valueOf(blockSize));
        file.addElement("fileSize").setText(String.valueOf(fileSize));
        file.addElement("blockId").setText(String.valueOf(blockId));
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(confDir), format);
        writer.write(document);
    }
}
