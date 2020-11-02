package com.example.attestsortie.ui.home;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.attestsortie.R;
import com.example.attestsortie.WebAppInterface;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private ImageButton btn_generate;
    private RadioGroup rd_group;
    private RadioButton cb_animaux;
    private RadioButton cb_course;
    private RadioButton cb_travail;
    private RadioButton cb_handi;
    private RadioButton cb_sante;
    private RadioButton cb_visite;
    private RadioButton cb_ecole;
    private WebView browser;
    private SharedPreferences myshared;
    private String cb="rien";


    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ImageButton generate_btn=root.findViewById(R.id.btn_generate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            generate_btn.setBackground(getActivity().getDrawable(R.drawable.button_generate));
        }

        //get view elements
        btn_generate=root.findViewById(R.id.btn_generate);
        rd_group=root.findViewById(R.id.att_rd_grp);
        cb_animaux=root.findViewById(R.id.att_rd_sport);
        cb_course=root.findViewById(R.id.att_rd_achat);
        cb_travail=root.findViewById(R.id.att_rd_travail);
        cb_handi=root.findViewById(R.id.att_rd_handi);
        cb_sante=root.findViewById(R.id.att_rd_sante);
        cb_visite=root.findViewById(R.id.att_rd_visite);
        cb_ecole=root.findViewById(R.id.att_rd_enfants);

        //WEBVIEW CONFIG
        browser=(WebView)root.findViewById(R.id.attest_web);
        browser.setWebViewClient(new WebViewClient());
        browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setAppCachePath(getContext().getCacheDir().getAbsolutePath());
        browser.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        browser.getSettings().setDatabaseEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setUseWideViewPort(true);
        browser.getSettings().setLoadWithOverviewMode(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new WebAppInterface(getContext()), "Android");
        browser.loadUrl("https://media.interieur.gouv.fr/deplacement-covid-19/");


        browser.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {


                    WebAppInterface mwai = new WebAppInterface(getContext());
                    browser.loadUrl(mwai.getBase64StringFromBlobUrl(url));

            }
        });


        //GENERATE
        btn_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb="rien";
                if(cb_animaux.isChecked()){
                   cb="checkbox-sport_animaux";
                }else{
                    if(cb_course.isChecked()){
                        cb="checkbox-achats";
                    }else{
                        if(cb_handi.isChecked()){
                            cb="checkbox-handicap";
                        }else{
                            if(cb_ecole.isChecked()){
                                cb="checkbox-enfants";
                            }else{
                                if(cb_visite.isChecked()){
                                    cb="checkbox-famille";
                                }else{
                                    if(cb_sante.isChecked()){
                                        cb="checkbox-sante";
                                    }else{
                                        if(cb_travail.isChecked()){
                                            cb="checkbox-travail";
                                        }else{
                                            cb="rien";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println("INFOS------------> generation ");
                myshared = getContext().getSharedPreferences("form_data", MODE_PRIVATE);
                SharedPreferences.Editor ed;
                if (myshared.contains("allisok")) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) == true)
                        {
                            explain();
                        }
                        else
                        {
                            askForPermission();
                        }
                    }
                    else {
                        if(cb.equals("rien")){
                            //on notifie
                            Toast pasOkToats=Toast.makeText(getContext(), "Cochez d'abord un motif !", Toast.LENGTH_SHORT);
                            pasOkToats.getView().setBackgroundColor(Color.RED);
                            pasOkToats.show();
                        }else {
                            rempliretenvoyer(browser, "", cb);


                        }
                    }
                }
                else{
                    //on notifie
                    Toast pasOkToats=Toast.makeText(getContext(), "Remplissez vos informations dans les paramètres !", Toast.LENGTH_SHORT);
                    pasOkToats.getView().setBackgroundColor(Color.RED);
                    pasOkToats.show();
                }

            }
        });


        return root;
    }





    public void rempliretenvoyer(WebView view, String url,String checkbox) {
        String prenom=myshared.getString("prenom","inconnu");
        String nom=myshared.getString("nom","inconnu");
        String daten=myshared.getString("datenaiss","00/00/0000");
        String lieun=myshared.getString("villenaiss","inconnu");
        String adresse=myshared.getString("adresse","inconnu");
        String city=myshared.getString("ville","inconnu");
        String cp=myshared.getString("cp","00000");
        SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat shfr = new SimpleDateFormat("HH:MM");
        Date today=new Date();
        String today_string_format=sdfr.format(today);
        System.out.println(today_string_format);
        String now_hour_format=shfr.format(today);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.evaluateJavascript("javascript:document.getElementById('field-firstname').value = '"+prenom+"';" +
                    "javascript:document.getElementById('field-lastname').value = '"+nom+"';" +
                    "javascript:document.getElementById('field-birthday').value = '"+daten+"';" +
                    "javascript:document.getElementById('field-placeofbirth').value = '"+lieun+"';" +
                    "javascript:document.getElementById('field-address').value = '"+adresse+"';" +
                    "javascript:document.getElementById('field-city').value = '"+city+"';" +
                    "javascript:document.getElementById('field-zipcode').value = '"+cp+"';" +
                    "javascript:document.getElementById('field-datesortie').value = '"+today_string_format+"';" +
                    "javascript:document.getElementById('field-heuresortie').value = '"+now_hour_format+"';" +
                    "javascript:document.getElementById('checkbox-sport_animaux').checked = false;" +
                    "javascript:document.getElementById('checkbox-achats').checked = false;" +
                    "javascript:document.getElementById('checkbox-handicap').checked = false;" +
                    "javascript:document.getElementById('checkbox-enfants').checked = false;" +
                    "javascript:document.getElementById('checkbox-famille').checked = false;" +
                    "javascript:document.getElementById('checkbox-sante').checked = false;" +
                    "javascript:document.getElementById('checkbox-travail').checked = false;" +
                    "javascript:document.getElementById('"+cb+"').checked = true;" +
                    "javascript:document.getElementById('generate-btn').click();", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    System.out.println("JS----------> " + s);
                }
            });
        }else{
            Toast pasOkToats=Toast.makeText(getContext(), "INCOMPATIBLE ANDROID VERSION", Toast.LENGTH_LONG);
            pasOkToats.getView().setBackgroundColor(Color.RED);
            pasOkToats.show();
        }



    }










    private void explain()
    {
        Snackbar.make(getView(), "Cette permission est nécessaire pour enregistrer le pdf", Snackbar.LENGTH_LONG).setAction("Activer", new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //demander l'autorisation
            }
        }).show();
    }




    private void askForPermission()
    {
        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE }, 2);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == 2)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                rempliretenvoyer(browser,"",cb);

            }
            else
            {
                explain();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



}