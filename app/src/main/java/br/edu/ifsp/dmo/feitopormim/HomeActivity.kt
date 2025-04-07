package br.edu.ifsp.dmo.feitopormim

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.feitopormim.databinding.ActivityHomeBinding

class HomeActivity: AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonExit.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish();
        }
    }
}