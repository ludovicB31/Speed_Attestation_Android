package com.example.attestsortie;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebAppInterface {
    Context mContext;
    String data;

    public WebAppInterface(Context ctx){
        this.mContext=ctx;
    }



    public void sendData(String data) {
        this.data=data;
    }
    @JavascriptInterface
    public void getBase64FromBlobData(String blobdata) throws IOException {
        System.out.println("INFO CALL------>  getBase64FromBlobData");
        convertBase64StringToPdfAndStoreIt(blobdata);
    }
    public static String getBase64StringFromBlobUrl(String blobUrl) {
        if(blobUrl.startsWith("blob")){
            return "javascript: var xhr = new XMLHttpRequest();" +
                    "xhr.open('GET', '"+ blobUrl +"', true);" +
                    "xhr.setRequestHeader('Content-type','application/pdf');" +
                    "xhr.responseType = 'blob';" +
                    "xhr.onload = function(e) {" +
                    "    if (this.status == 200) {" +
                    "        var blobPdf = this.response;" +
                    "        var reader = new FileReader();" +
                    "        reader.readAsDataURL(blobPdf);" +
                    "        reader.onloadend = function() {" +
                    "            base64data = reader.result;" +
                    "            Android.getBase64FromBlobData(base64data);" +
                    "        }" +
                    "    }" +
                    "};" +
                    "xhr.send();";
        }
        System.out.println("DEBUG-------------> It is not a Blob URL in BlobURL conversion");
        return "javascript: console.log('It is not a Blob URL');";
    }
    private void convertBase64StringToPdfAndStoreIt(String base64PDf) throws IOException {

        SimpleDateFormat sdfr = new SimpleDateFormat("dd_MM_yyyy");
        SimpleDateFormat shfr = new SimpleDateFormat("HH_mm_ss");
        Date today=new Date();
        String today_string_format=sdfr.format(today);
        String now_hour_format=shfr.format(today);


        String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
        final File dwldsPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Attestation_"+today_string_format+"_a_"+now_hour_format+"h"+".pdf");
        byte[] pdfAsBytes = Base64.decode(base64PDf.replaceFirst("^data:application/pdf;base64,", ""), 0);
        FileOutputStream os;
        os = new FileOutputStream(dwldsPath, false);
        os.write(pdfAsBytes);
        os.flush();

        if(dwldsPath.exists()) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            Uri apkURI = FileProvider.getUriForFile(mContext,mContext.getApplicationContext().getPackageName() + ".provider", dwldsPath);
            intent.setDataAndType(apkURI, MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext,1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            String CHANNEL_ID = "MYCHANNEL";
            final NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel= new NotificationChannel(CHANNEL_ID,"name", NotificationManager.IMPORTANCE_LOW);
                Notification notification = new Notification.Builder(mContext,CHANNEL_ID)
                        .setContentText("Attestation téléchargé ("+now_hour_format+"h)")
                        .setContentTitle("File downloaded")
                        .setContentIntent(pendingIntent)
                        .setChannelId(CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.sym_action_chat)
                        .build();
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(notificationChannel);
                    notificationManager.notify(765, notification);
                }

            }


            //on notifie
            Toast okToast = Toast.makeText(mContext, "PDF téléchargé dans 'Télechargements'", Toast.LENGTH_LONG);
            okToast.getView().setBackgroundColor(Color.GREEN);
            okToast.show();
        }
        else {
            //on notifie
            Toast pasOkToats=Toast.makeText(mContext, "Impossible de télécharge le pdf :/", Toast.LENGTH_LONG);
            pasOkToats.getView().setBackgroundColor(Color.RED);
            pasOkToats.show();
        }

    }
}