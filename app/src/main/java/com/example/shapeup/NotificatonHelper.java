package com.example.shapeup;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class NotificatonHelper extends ContextWrapper {
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    public static final String channelId="CHANNEL_ID";
    public static final String channelName="CHANNEL_NAME";
    String med="", uid="";
    private NotificationManager mManager;
    private Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.medicine);

    public NotificatonHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            auth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            uid= Objects.requireNonNull(auth.getCurrentUser()).getUid();

            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannel() {
        NotificationChannel channel=new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if(mManager==null){
            mManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        firestore.collection("Reminders").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    med = task.getResult().getString("Medicine Name");
                } else {
                    Toast.makeText(NotificatonHelper.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            return new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setContentTitle("Medicine Reminder")
                    .setContentText("Its time to take your medicine: " + med)
                    .setSound(soundUri)
                    .setSmallIcon(R.mipmap.ic_launcher_round_adaptive_fore)
                    .setLargeIcon(bitmap);
    }
}
