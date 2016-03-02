import com.onion.master.FileConf;
import org.junit.Test;

import java.io.File;

/**
 * Created by sunhonglin on 16-3-2.
 */
public class FileConfTest {
    String confSrc = "/home/sunhonglin/work/onion/common/master/fileconf.xml";
    @Test
    public void testWrite() throws Exception{
        FileConf conf = new FileConf(new File(confSrc));
        conf.setFileInfo("sunhonglin", 1, 2, 3);
    }

    @Test
    public void testRead() {
        FileConf conf = new FileConf(new File(confSrc));
        System.out.println(conf.getFilename());
        System.out.println(conf.getBlockSize());
        System.out.println(conf.getFileSize());
        System.out.println(conf.getBlockId());
    }
}
