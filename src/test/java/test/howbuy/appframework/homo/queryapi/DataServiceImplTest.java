package test.howbuy.appframework.homo.queryapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import net.sf.json.JSONObject;

import com.howbuy.appframework.homo.queryapi.DataServiceImpl;
import com.howbuy.appframework.homo.queryapi.JsonQueryResp;
import com.howbuy.appframework.homo.queryapi.QueryUtils;

public class DataServiceImplTest
{

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        long t2 = System.currentTimeMillis();
        QueryUtils.getHomoConnection();
        System.out.println(" cost =====================: " + (System.currentTimeMillis() - t2) / 1000.0);
        
        DataServiceImpl svcImpl = new DataServiceImpl();
        InputStream is = null;
        File r = new File("sample/query/json_query_req.txt");
        is = new FileInputStream(r);
        byte[] data = new byte[(int) r.length()];
        int curr = 0;
        int b = -1;
        while ((b = is.read()) >= 0)
        {
            data[curr++] = (byte) b;
        }
        is.close();
        
        String queryJsonStr = new String(data, "utf-8");
        JsonQueryResp resultset = svcImpl.query(queryJsonStr);
        JSONObject jsonResp = JSONObject.fromObject(resultset);
        System.out.println("==============================================");
        System.out.println(jsonResp.toString());
    }

}
