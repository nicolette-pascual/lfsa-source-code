package com.lfsa.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lfsa.Activities.LoginActivities.LoginActivity;
import com.lfsa.MainActivity;
import com.lfsa.Activities.MenuActivities.MenuActivity;
import com.lfsa.R;
import com.lfsa.Activities.MenuActivities.MenuNavBarActivities.RatingActivity;
import com.lfsa.GettersSetters.Users;
import com.lfsa.Activities.ViewOrderActivity;
import com.lfsa_foodstallcrew.Activities.ViewOrdersActivity_Crew;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "channel1";
    public String chosenFoodStall, foodStall, status, customer, customerId, message;
    FirebaseUser user;
    DatabaseReference databaseReference;
    LoginActivity login = new LoginActivity();
    MainActivity main = new MainActivity();

    Users users = new Users();
    Intent intent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

       customer = users.getCustomer();
       customerId = users.getCustomerId();

        chosenFoodStall = remoteMessage.getData().get("chosenFoodStall").toString();
        status = remoteMessage.getData().get("status").toString();


        if(status.equals("Food is being cooked") || status.equals("Ready for pick-up")) {
            intent = new Intent(this, ViewOrderActivity.class);
            intent.putExtra("chosenFoodStall", chosenFoodStall);
            intent.putExtra("customer", customer);
            intent.putExtra("customerId", customerId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            message = remoteMessage.getNotification().getBody();
        }

        else if (status.equals("Accepted")){
            intent = new Intent(this, ViewOrderActivity.class);
            intent.putExtra("chosenFoodStall", chosenFoodStall);
            intent.putExtra("customer", customer);
            intent.putExtra("customerId", customerId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            message = "Your Order has been accepted by " + chosenFoodStall;
        }


        else if (status.equals("Pending")){
            intent = new Intent(this, ViewOrderActivity.class);
            intent.putExtra("chosenFoodStall", chosenFoodStall);
            intent.putExtra("customer", customer);
            intent.putExtra("customerId", customerId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            message = "Thank you for ordering at " + chosenFoodStall +". Please wait for further updates about your order.";
        }

        else if (status.equals("Finished")){
            intent = new Intent(this, RatingActivity.class);
            intent.putExtra("chosenFoodStall", chosenFoodStall);
            intent.putExtra("customer", customer);
            intent.putExtra("customerId", customerId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            message = "Thank you for ordering at " + chosenFoodStall + ". Enjoy!";
        }

        else if (status.equals("Declined")){
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("chosenFoodStall", chosenFoodStall);
            intent.putExtra("customer", customer);
            intent.putExtra("customerId", customerId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            message = "Your Order has been declined by " + chosenFoodStall;
        }

        else {
            intent = new Intent(this, MenuActivity.class);
            intent.putExtra("chosenFoodStall", chosenFoodStall);
            intent.putExtra("customer", customer);
            intent.putExtra("customerId", customerId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            message = remoteMessage.getNotification().getBody();
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setContentTitle("LFSA");
        notificationBuilder.setContentText(message);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.drawable.ic_notifications_none_black_24dp);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }

}
