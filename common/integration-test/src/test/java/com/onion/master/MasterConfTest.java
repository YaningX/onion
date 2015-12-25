package com.onion.master;

import com.onion.conf.Conf;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by sunhonglin on 15-12-25.
 */
public class MasterConfTest {
    @Test
    public void readXMLTest() throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(System.getProperty("user.dir") + "/conf.xml"));
        Element root = document.getRootElement();
        int wordSize = Integer.parseInt(root.element("wordSize").getText());
        int packetSize = Integer.parseInt(root.element("packetSize").getText());
        Assert.assertEquals(8, wordSize);
        Assert.assertEquals(8, packetSize);
        Element address = root.element("InetAddress");
        List<Element> addresses = address.elements();
        for (Element element : addresses) {
            System.out.println(element.getText());
        }
    }

    @Test
    public void readConfWithXMLTest() throws Exception{
        MasterConf conf= new MasterConf(new File(System.getProperty("user.dir") + "/conf.xml"));
        Assert.assertEquals(6, conf.getDataWorkerAmount());
        Assert.assertEquals(3, conf.getParityWorkerAmount());
        Assert.assertEquals(8, conf.getWordSize());
        Assert.assertEquals(8, conf.getPacketSize());
        Assert.assertEquals("CauchyGoodRSCoder", conf.getErasureCodeType());
        System.out.println(conf.getErasureCodeType());
        List<InetSocketAddress> addresses = conf.getWorkerAddresses();
        for (InetSocketAddress add : addresses) {
            System.out.println(add);
        }
    }

    @Test
    public void generateWorkerConfTest() throws Exception{
        File confSrc  = new File(System.getProperty("user.dir") + "/src/test/conf");
        MasterConf masterConf = new MasterConf(new File(System.getProperty("user.dir") + "/conf.xml"));
        List<InetSocketAddress> addresses = masterConf.getWorkerAddresses();
        for (int i = 0; i < addresses.size(); i++) {
            InetSocketAddress address = addresses.get(i);
            File file = new File(confSrc, "worker" + (i+1) + ".conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            Conf workerConf = new Conf();
            workerConf.loadConf(file);
            System.out.println(address.toString());
            workerConf.setString("IP", address.getHostString());
            workerConf.setInt("port", address.getPort());
            workerConf.saveConf(file);
        }
    }
}
