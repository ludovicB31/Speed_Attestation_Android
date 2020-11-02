package com.example.attestsortie.ui.dashboard;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.attestsortie.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DashboardFragment extends Fragment {
    private EditText et_nom;
    private EditText et_prenom;
    private EditText et_villenaiss;
    private EditText et_ville;
    private EditText et_adress;
    private EditText et_cp;
    private EditText et_datenaiss;


    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);


        //get view elements
        et_nom=root.findViewById(R.id.et_nom);
        et_prenom=root.findViewById(R.id.et_prenom);
        et_villenaiss=root.findViewById(R.id.et_att_lieun);
        et_datenaiss=root.findViewById(R.id.et_date);
        et_ville=root.findViewById(R.id.et_att_city);
        et_cp=root.findViewById(R.id.et_att_cp);
        et_adress=root.findViewById(R.id.et_att_adress);


        //recuperation des champs s'ils existent
        SharedPreferences sharedPrefs = getContext().getSharedPreferences("form_data", MODE_PRIVATE);
        if (sharedPrefs.contains("allisok")) {

            et_nom.setText(sharedPrefs.getString("nom","Nom"));
            et_prenom.setText(sharedPrefs.getString("prenom","Prenom"));
            et_ville.setText(sharedPrefs.getString("ville","Ville"));
            et_cp.setText(sharedPrefs.getString("cp","00000"));
            et_adress.setText(sharedPrefs.getString("adresse","Adresse"));
            et_datenaiss.setText(sharedPrefs.getString("datenaiss","00/00/0000"));
            et_villenaiss.setText(sharedPrefs.getString("villenaiss","Ville naissance"));



        }



        TextView btn_save=root.findViewById(R.id.btn_save_data);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPrefs = getContext().getSharedPreferences("form_data", MODE_PRIVATE);
                SharedPreferences.Editor ed;
                if (!sharedPrefs.contains("initialized")) {
                    ed = sharedPrefs.edit();

                    //Indicate that the default shared prefs have been set
                    ed.putBoolean("initialized", true);


                    ed.commit();
                }

                //get form infos
                int nb_errors=0;
                String error_txt="";

                String nom=et_nom.getText().toString();
                if(nom.equals(null) || nom.equals("")){
                    nb_errors+=1;
                    error_txt+="| Le nom est incorrect";
                }

                String prenom=et_prenom.getText().toString();
                if(prenom.equals(null) || prenom.equals("")){
                    nb_errors+=1;
                    error_txt+="| Le prenom est incorrect";
                }

                String villenaiss=et_villenaiss.getText().toString();
                if(villenaiss.equals(null) || villenaiss.equals("")){
                    nb_errors+=1;
                    error_txt+="| Le lieu de naissance est incorrect";
                }

                String datenaiss=et_datenaiss.getText().toString();
                if(datenaiss.equals(null) || datenaiss.equals("") || !isValidFormat("dd/MM/yyyy", datenaiss, Locale.ENGLISH)){
                    nb_errors+=1;
                    error_txt+="| La date de naissance doit etre au format JJ/MM/AAAA";
                }

                String adress=et_adress.getText().toString();
                if(adress.equals(null) || adress.equals("")){
                    nb_errors+=1;
                    error_txt+="| L'adresse est incorrecte";
                }

                String ville=et_ville.getText().toString();
                if(ville.equals(null) || ville.equals("")){
                    nb_errors+=1;
                    error_txt+="| Le vile est incorrecte";
                }

                String cp=et_cp.getText().toString();
                if(cp.equals(null) || cp.equals("") || cp.length()!=5){
                    nb_errors+=1;
                    error_txt+="| Le code postal est incorrect (5 chiffres)";
                }

                if(nb_errors==0){

                    //on sauve les infos
                    ed = sharedPrefs.edit();
                    ed.putBoolean("allisok",true);
                    ed.putString("nom",nom);
                    ed.putString("prenom",prenom);
                    ed.putString("ville", ville);
                    ed.putString("adresse",adress);
                    ed.putString("cp",cp);
                    ed.putString("villenaiss",villenaiss);
                    ed.putString("datenaiss",datenaiss);
                    ed.commit();

                    //on notifie
                    Toast okToast=Toast.makeText(getContext(),"Informations mises a jour",Toast.LENGTH_LONG);
                    okToast.getView().setBackgroundColor(Color.GREEN);
                    okToast.show();
                }
                else{
                    Toast pasOkToats=Toast.makeText(getContext(), nb_errors+" Erreur: "+error_txt, Toast.LENGTH_LONG);
                    pasOkToats.getView().setBackgroundColor(Color.RED);
                    pasOkToats.show();
                }

            }
        });
        return root;
    }



    public static boolean isValidFormat(String format, String value, Locale locale) {
        LocalDateTime ldt = null;
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, locale);

        try {
            ldt = LocalDateTime.parse(value, fomatter);
            String result = ldt.format(fomatter);
            return result.equals(value);
        } catch (DateTimeParseException e) {
            try {
                LocalDate ld = LocalDate.parse(value, fomatter);
                String result = ld.format(fomatter);
                return result.equals(value);
            } catch (DateTimeParseException exp) {
                try {
                    LocalTime lt = LocalTime.parse(value, fomatter);
                    String result = lt.format(fomatter);
                    return result.equals(value);
                } catch (DateTimeParseException e2) {
                    // Debugging purposes
                    //e2.printStackTrace();
                }
            }
        }

        return false;
    }


}