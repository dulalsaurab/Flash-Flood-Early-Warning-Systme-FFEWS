
package majorproject;

//================================================ Java code sample ================================================//

import java.net.*;
import java.io.*;

public class SendSms {

    static public void sendSMS(String message,String toNum) {
        try {
            // Construct data
            String data = "";
            
            /////////////////////////////////////////for bulksms//////////////////////////////////////////////////////
            
            data += "username=" + URLEncoder.encode("suraz", "ISO-8859-1");
            data += "&password=" + URLEncoder.encode("9813641099", "ISO-8859-1");
            data += "&message=" + URLEncoder.encode(message, "ISO-8859-1");
            data += "&want_report=1";
            data += "&msisdn="+toNum;
            
            URL url = new URL("http://bulksms.2way.co.za:80/eapi/submission/send_sms/2/2.0"); //use port 5567 or port 80
            
            ////////////////////////////////////////////////end for bulk sms//////////////////////////////////////////////////////////////
            
            
            ////////////////////////////////////////////////clickatell sms//////////////////////////////////////////////////////////
//            data += "user=" + URLEncoder.encode("surajpandey", "ISO-8859-1");
//            data += "&password=" + URLEncoder.encode("GUUMLZecMDVZUM", "ISO-8859-1");
//            data += "&api_id=" + URLEncoder.encode("3488067", "ISO-8859-1");
//            data += "&to="+toNum;
//            data += "&text=" + URLEncoder.encode(message, "ISO-8859-1");
//            
////            URL url = new URL("http://api.clickatell.com/http/sendmsg?user=surajpandey&password=GUUMLZecMDVZUM&api_id=3488067&to=9845390340&text=hello suraj");
//            URL url = new URL("http://api.clickatell.com/http/sendmsg");
            
            /////////////////////////////////////////////////end clickatell sms//////////////////////////////////////////////////////

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Print the response output...
                System.out.println(line);
            }
            wr.close();
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


