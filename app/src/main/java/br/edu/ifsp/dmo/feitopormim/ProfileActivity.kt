package br.edu.ifsp.dmo.feitopormim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.feitopormim.databinding.ActivityProfileBinding
import br.edu.ifsp.dmo.feitopormim.util.Base64Converter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val galeria = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()) {
                uri ->
            if (uri != null) {
                binding.profileImage.setImageURI(uri)
            } else {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
            }
        }
        binding.buttonChangePicture.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
            Toast.makeText(this, "Foto alterada com sucesso", Toast.LENGTH_LONG).show()
        }

        binding.save.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()
            if (firebaseAuth.currentUser != null) {
                val email = firebaseAuth.currentUser!!.email.toString()
                val username = binding.textUserName.text.toString()
                val nomeCompleto = binding.textNameComplete.text.toString()

                val fotoPerfilString = if (binding.profileImage.drawable != null && binding.profileImage.drawable.constantState != resources.getDrawable(R.drawable.empty_profile).constantState) {
                    Base64Converter.drawableToString(binding.profileImage.drawable)
                } else{
                    "default"
                }

                val db = Firebase.firestore
                val dados = hashMapOf(
                    "nomeCompleto" to nomeCompleto,
                    "username" to username,
                    "fotoPerfil" to fotoPerfilString
                )
                db.collection("usuarios").document(email)
                    .set(dados)
                    .addOnSuccessListener {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

}