package br.edu.ifsp.dmo.feitopormim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import br.edu.ifsp.dmo.feitopormim.databinding.ActivitySingupBinding
import com.google.firebase.auth.FirebaseAuth

class SingUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance();
        binding.buttonCreate.setOnClickListener {
            val email = binding.textEmail.text.toString()
            val senha = binding.textPassword.text.toString()
            val senhaConfirmada = binding.textPasswordConfirm.text.toString()

            if(senha == senhaConfirmada){
                firebaseAuth
                    .createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            Toast.makeText(this, "Usuario criado", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
            } else{
                    Toast.makeText(this, "As SENHAS não são iguais", Toast.LENGTH_LONG).show()
            }
        }
    }
}