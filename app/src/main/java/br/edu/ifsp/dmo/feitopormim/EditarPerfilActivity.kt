package br.edu.ifsp.dmo.feitopormim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.feitopormim.databinding.ActivityEditarPerfilBinding
import br.edu.ifsp.dmo.feitopormim.util.Base64Converter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class EditarPerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()
        val db = Firebase.firestore
        val email = firebaseAuth.currentUser!!.email.toString()

        db.collection("usuarios").document(email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        val imageString = document.getString("fotoPerfil")
                        val username = document.getString("username") ?: ""
                        val nomeCompleto = document.getString("nomeCompleto") ?: ""

                        binding.usernameEditText.setText(username)
                        binding.nameEditText.setText(nomeCompleto)

                        if (imageString.isNullOrEmpty() || imageString == "default") {
                            binding.userImage.setImageResource(R.drawable.empty_profile)
                        } else {
                            val bitmap = Base64Converter.stringToBitmap(imageString)
                            binding.userImage.setImageBitmap(bitmap)
                        }
                    } else {
                        binding.userImage.setImageResource(R.drawable.empty_profile)
                        binding.usernameEditText.setText("")
                        binding.nameEditText.setText("")
                    }
                } else {
                    Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                }
            }

        val galeria = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()) {
                uri ->
            if (uri != null) {
                binding.userImage.setImageURI(uri)
            } else {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
            }
        }

        binding.alterarFotoButton.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.close.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.salvarButton.setOnClickListener {
            val novoUsername = binding.usernameEditText.text.toString()
            val novoNomeCompleto = binding.nameEditText.text.toString()

            val fotoPerfilString = if (binding.userImage.drawable != null && binding.userImage.drawable.constantState != resources.getDrawable(R.drawable.empty_profile).constantState) {
                Base64Converter.drawableToString(binding.userImage.drawable)
            } else {
                "default"
            }

            val updates = hashMapOf(
                "username" to novoUsername,
                "nomeCompleto" to novoNomeCompleto,
                "fotoPerfil" to fotoPerfilString
            )

            db.collection("usuarios").document(email)
                .update(updates as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show()
                }
        }
    }
}