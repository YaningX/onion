package com.onion.master;

import com.onion.worker.Worker;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * Created by sunhonglin on 16-1-3.
 */
public class MasterTest {
    private static String srcPath;
    private static String recoverPath;
    private static String masterConfSrc;
    private static Master master;

    @BeforeClass
    public static void step() throws Exception{
        srcPath = "/home/sunhonglin/work/onion/common/integration-test/pom.xml";
        recoverPath = "/home/sunhonglin/work/onion/common/integration-test/target/recover_pom.xml";
        masterConfSrc = "/home/sunhonglin/work/onion/common/master/conf.xml";
        master = new Master(new File(masterConfSrc));
        Worker[] workers = new Worker[9];
        for (int i = 0; i < workers.length; i++) {
            Properties property = new Properties();
            property.load(new FileInputStream(System.getProperty("user.dir") +
                    "/src/test/conf/worker" + (i+1) + ".conf"));
            workers[i] = new Worker(new InetSocketAddress(property.getProperty("IP"),
                    Integer.parseInt(property.getProperty("port"))), property.getProperty("backendDir"));
            workers[i].process();
        }
    }

    @AfterClass
    public static void clear() {
    }

    @Test
    public void masterWriteAndReadTest() {
        master.write(srcPath);
        master.read(recoverPath);
    }

}
