package com.onion.master;

import com.onion.DBUtil.DBUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

/**
 * Created by sunhonglin on 16-1-3.
 */
public class MasterTest {
    private static String srcPath;
    private static String recoverPath = System.getProperty("user.dir" + "/target/recover_pom.xml");
    private static String masterConfSrc;
    private static String workerConfSrc;
    private static Master master;

    @BeforeClass
    public static void step() throws Exception{
        srcPath = "/home/sunhonglin/work/onion/common/integration-test/pom.xml";
        recoverPath = "/home/sunhonglin/work/common/integration-test/target/recover_pom.xml";
        masterConfSrc = "/home/sunhonglin/work/onion/common/master/conf.xml";
        workerConfSrc = "/home/sunhonglin/work/onion/common/worker/conf.xml";
        master = new Master(new File(masterConfSrc));
        DBUtil util = new DBUtil();
        util.createTable();
    }

    @Test
    public void masterWriteTest() {
        master.write(srcPath);
    }

    @Test
    public void masterReadTest() {
        master.read(srcPath, recoverPath);
    }
}
