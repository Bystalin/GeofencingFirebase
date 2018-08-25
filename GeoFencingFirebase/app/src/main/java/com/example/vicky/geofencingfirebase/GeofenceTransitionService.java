package com.example.vicky.geofencingfirebase;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.vicky.geofencingfirebase.firebase.Mensaje;
import com.example.vicky.geofencingfirebase.firebase.Notification;
import com.example.vicky.geofencingfirebase.firebase.ServiceApi;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//fIREBASE

public class GeofenceTransitionService extends IntentService {

    private static final String TAG = GeofenceTransitionService.class.getSimpleName();
    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    public GeofenceTransitionService() {
        super(TAG);
    }
    public String asunto="NOTIFICACION DE GEOFENCE!";

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Manejador de  errors
        if (geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;    }
        // Obtiene el tipo de transicion
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Verificar si el tipo de transición es de interés
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            // Get the geofence QUE SE ACTIVO
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Obtiene los detalles de la transicion en tipo string
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geoFenceTransition, triggeringGeofences );
            // ENVIAR  DETALLE DE notification COMO UN String
            sendNotification( geofenceTransitionDetails );
        }

    }


    private String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }


    private String getGeofenceTransitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // obtener la identificación de cada geofence desencadenada
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }

        String status = null;
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
            status = "Entrando en ";
        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = "Saliendo de ";
        return status + TextUtils.join( ", ", triggeringGeofencesList);
    }


    private void sendNotification( String msg ) {
        /*Log.i(TAG, "sendNotification: " + msg);

        // Intent to start the main Activity
        Intent notificationIntent = MainActivity.makeNotificationIntent(
                getApplicationContext(), msg
        );

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));*/


        String user = MainActivity.usuario;
        String content = user;

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        contruirmensaje(user,msg);

    }


    // Create notification
    /*private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_location)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText(asunto)
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }*/


    OkHttpClient client = new OkHttpClient.Builder().writeTimeout(10, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization",
                            "key=AAAAGzWlPNQ:APA91bEvRpM2tQAeMfovroI3i31_vESvy3t_p6Yp-GyOKXHJOHRz33GMQ_2zUqCFvH3V3w4_HHxoN91wi0UXVDKWFPVGn7B_UvA_oKQYv57PK1eIC8QyGg1gkIwPYY-2jR2oc_zqWqlg")
                    .addHeader("Content-Type", "application/json")
                    .build();
            return chain.proceed(newRequest);
        }
    }).build();

    Retrofit retrofit = new Retrofit.Builder()
            .client(client)
            .baseUrl("https://fcm.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    ServiceApi restClient = retrofit.create(ServiceApi.class);


    public void contruirmensaje(String usuarioSesion,String msg){
        Notification notification = new Notification();
        notification.setTitle("Notificación Firebase");
        notification.setBody(usuarioSesion + " " + msg);
        notification.getClickAction("TOP_STORY_ACTIVITY");

        Mensaje mensaje1 = new Mensaje();
        mensaje1.setTo("/topics/news");
        mensaje1.setNotification(notification);
        sent(mensaje1);
    }


    public void sent(Mensaje mensaje){
        Call<Mensaje> call = restClient.create(mensaje);
        call.enqueue(new Callback<Mensaje>() {
            @Override
            public void onResponse(Call<Mensaje> call, retrofit2.Response<Mensaje> response) {
                if (response.code() == 200) {

                }
            }
            @Override
            public void onFailure(Call<Mensaje> call, Throwable t) {
            }
        });
    }




    //////////////////////////////

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceTransitionService(String name) {
        super(name);
    }

}
