package br.edu.ifsp.dmo.feitopormim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.feitopormim.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.currentUser != null){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.textEmail.text.toString()
            val senha = binding.textPassword.text.toString()
            firebaseAuth
                .signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.buttonCreate.setOnClickListener {
            startActivity(Intent(this, SingUpActivity::class.java))
            finish()
        }
    }
}