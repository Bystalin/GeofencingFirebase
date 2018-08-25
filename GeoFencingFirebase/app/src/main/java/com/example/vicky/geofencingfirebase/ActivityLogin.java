package com.example.vicky.geofencingfirebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityLogin extends AppCompatActivity {

    EditText TxtvUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
    }


    public void btEnviar(View view){
        TxtvUsuario = (EditText)this.findViewById(R.id.TxtUsuario);
        if(!TxtvUsuario.getText().toString().isEmpty()){
            //Creamos el Intent
            Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
            //Creamos la información a pasar entre actividades - Pares Key-Value
            Bundle b = new Bundle();
            b.putString("usuario", TxtvUsuario.getText().toString());
            //Añadimos la información al intent
            intent.putExtras(b);
            startActivity(intent);
        }else{
            Toast.makeText(this,"Ingrese un Usuario",Toast.LENGTH_SHORT).show();
        }
    }

}
