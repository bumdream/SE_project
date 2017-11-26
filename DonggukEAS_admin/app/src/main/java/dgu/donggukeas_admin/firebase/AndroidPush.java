package dgu.donggukeas_admin.firebase;

/**
 * Created by hanseungbeom on 2017. 11. 10..
 */
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dgu.donggukeas_admin.util.Constants;

public class AndroidPush {
    /**
     * Sends notification to mobile, YOU DON'T NEED TO UNDERSTAND THIS METHOD
     */
    public static void sendPushNotification(final String subjectCode,final int notiType, final int week,final String subjectName,final String studentId,final String deviceToken) {
        new Thread(new Runnable() {
            @Override public void run() { //
                try {
                    //Thread.sleep(5000);
                    String pushMessage = "{\"data\":{\"" +
                            "subjectCode\":\"" +
                            subjectCode +
                            "\",\"week\":\"" +
                            String.valueOf(week) +
                            "\",\"subjectName\":\"" +
                            subjectName +
                            "\",\"notiType\":\"" +
                            String.valueOf(notiType) +
                            "\",\"studentId\":\"" +
                            studentId+
                            "\"},\"to\":\"" +
                            deviceToken +
                            "\"}";
                    // Create connection to send FCM Message request.
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "key=" + Constants.ServerKey);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Send FCM message content.
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(pushMessage.getBytes());

                    System.out.println(conn.getResponseCode());
                    System.out.println(conn.getResponseMessage());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();


    }
}