package test.howbuy.appframework.homo.queryapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 测试json查询接口请求报文.
 * @author li.zhang
 *
 */
public class TestJsonQueryReqJson
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
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
            String jsonStr = new String(data, "utf-8");
            // jsonStr = "[" + jsonStr + "]";
            JSONObject obj = JSONObject.fromObject(jsonStr);
            JSONObject jsonReq = (JSONObject) obj.get("query-json-req");
            String templateId = (String) jsonReq.get("template-id");
            System.out.println("templateId=" + templateId);

            JSONObject operation = jsonReq.getJSONObject("where")
                    .getJSONObject("operation");
            JSONArray rsArray = (JSONArray) operation.getJSONArray("resultset");
            for (int i = 0; i < rsArray.size(); i++)
            {
                JSONObject condition = rsArray.getJSONObject(i).getJSONObject(
                        "condition");
                if (null == condition)
                {
                    continue;
                }

                System.out.println(condition);

                String command = (String) condition.get("-command");
                System.out.println("command=" + command);
                JSONArray caseArray = (JSONArray) condition
                        .getJSONArray("case");
                for (int j = 0; j < caseArray.size(); j++)
                {
                    JSONObject jsonCase = caseArray.getJSONObject(j);
                    System.out.println(jsonCase);
                    System.out.println(jsonCase.get("-colume-name"));
                    System.out.println(jsonCase.get("-colume_value"));
                    System.out.println(jsonCase.get("-express_op"));
                }

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
