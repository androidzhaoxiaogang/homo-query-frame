package test.howbuy.appframework.homo.queryapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TestJson
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            InputStream is = null;
            File r = new File("d:\\r.txt");
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
            // jsonStr = "[" + jsonStr + "]";
            JSONObject obj = JSONObject.fromObject(jsonStr);
            JSONObject resObject = (JSONObject) obj.get("response");
            JSONArray array = (JSONArray) resObject.get("docs");
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject jsonObject = array.getJSONObject(i);
                System.out.println(jsonObject);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {

        }
    }
}
