package dgu.donggukeas_client.firebase;

/**
 * Created by hanseungbeom on 2017. 11. 13..
 */

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.Map;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import dgu.donggukeas_client.R;
import dgu.donggukeas_client.ui.ResultActivity;
import dgu.donggukeas_client.ui.SendWifiActivity;
import dgu.donggukeas_client.util.Constants;


public class MyFirebaseMessageService extends FirebaseMessagingService{

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            /*Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("studentId"));
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("subjectCode"));
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("week"));*/
            Log.d("#####","message data:"+remoteMessage.getData().get("notiType"));
            int isNotiType = Integer.parseInt(remoteMessage.getData().get("notiType"));
            //boolean isNormalMode = (remoteMessage.getData().get("notiType").equals(Constants.normal))?true:false;

            //앱이 현재 실행중이면
            if(isRunningInForeground()){

                //결과 페이지가 아니면
                if(isNotiType!=Constants.ATTENDANCE_RESULT){
                    //와이파이 전송 액티비티로
                    Intent intent = new Intent(this, SendWifiActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("studentId", remoteMessage.getData().get("studentId"));
                    intent.putExtra("subjectCode", remoteMessage.getData().get("subjectCode"));
                    intent.putExtra("week", Integer.parseInt(remoteMessage.getData().get("week")));
                    intent.putExtra("subjectName", remoteMessage.getData().get("subjectName"));
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(this, ResultActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("studentId", remoteMessage.getData().get("studentId"));
                    intent.putExtra("subjectCode", remoteMessage.getData().get("subjectCode"));
                    intent.putExtra("week", Integer.parseInt(remoteMessage.getData().get("week")));
                    intent.putExtra("subjectName", remoteMessage.getData().get("subjectName"));
                    startActivity(intent);
                }
            }

            //앱이 실행중이 아니면
            else{

                sendNotification(remoteMessage.getData(),isNotiType);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    protected boolean isRunningInForeground() {
        ActivityManager manager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        if (tasks.isEmpty()) {
            return false;
        }
        String topActivityName = tasks.get(0).topActivity.getPackageName();
        return topActivityName.equalsIgnoreCase(getPackageName());
    }
    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void sendNotification(Map<String,String> msg,int notiType) {

            if(notiType!=Constants.ATTENDANCE_RESULT){
                Intent intent = new Intent(this, SendWifiActivity.class);
                intent.putExtra("studentId", msg.get("studentId"));
                intent.putExtra("subjectCode", msg.get("subjectCode"));
                intent.putExtra("week", Integer.parseInt(msg.get("week")));
                intent.putExtra("subjectName", msg.get("subjectName"));

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);


                //TODO 출튀면 도망가는 그림으로 나오게
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle(msg.get("subjectName") + "[" + msg.get("subjectCode") + "]")
                                .setContentText(getString(R.string.info_noti))
                                .setSmallIcon(R.drawable.ic_logo)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
            else{
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("studentId", msg.get("studentId"));
                intent.putExtra("subjectCode", msg.get("subjectCode"));
                intent.putExtra("week", Integer.parseInt(msg.get("week")));
                intent.putExtra("subjectName", msg.get("subjectName"));

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);


                //TODO 출튀면 도망가는 그림으로 나오게
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle(msg.get("subjectName") + "[" + msg.get("subjectCode") + "]")
                                .setContentText(getString(R.string.info_result))
                                .setSmallIcon(R.drawable.ic_logo)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
            }

    }
}