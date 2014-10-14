package test.howbuy.appframework.homo.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.howbuy.appframework.homo.queryapi.analyzer.JsonResolver;

public class JsonResolverTest
{
    public static void main(String[] args) throws Exception
    {
        InputStream is = null;
        File r = new File("sample/query/json_query_req_v2.txt");
        is = new FileInputStream(r);
        byte[] data = new byte[(int) r.length()];
        int curr = 0;
        int b = -1;
        while ((b = is.read()) >= 0)
        {
            data[curr++] = (byte) b;
        }
        is.close();
        String jsonStr = new String(data, "utf-8");
        
        JsonResolver.getInstance().createExecSchedule(jsonStr);
    }
}
