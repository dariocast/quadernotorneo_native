package it.dariocast.quadernotorneo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class DettaglioPartita extends AppCompatActivity {
    TextView idTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio_partita);

        Intent incoming = getIntent();
        if (incoming.hasExtra("idPartita")) {
            idTv = findViewById(R.id.id_tv);
            int id = incoming.getIntExtra("idPartita",0);
            idTv.setText(String.format(Locale.ITALIAN,"%d", id));
        }
    }
}
