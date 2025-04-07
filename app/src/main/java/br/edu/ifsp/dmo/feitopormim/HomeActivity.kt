package br.edu.ifsp.dmo.feitopormim

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.feitopormim.databinding.ActivityHomeBinding

class HomeActivity: AppCompatActivity() {
    private lateinit var biding:ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biding.buttonExit.setOnClickListener {
            finish();
        }
    }
}