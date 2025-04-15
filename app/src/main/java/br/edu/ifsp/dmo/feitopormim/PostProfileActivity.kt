package br.edu.ifsp.dmo.feitopormim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.feitopormim.databinding.ActivityPostprofileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class PostProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostprofileBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityPostprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val galeria = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()) {
                uri ->
            if (uri != null) {
                binding.picturePost.setImageURI(uri)
            } else {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
            }
        }
        binding.buttonChangePicturePost.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
            Toast.makeText(this, "Foto alterada com sucesso", Toast.LENGTH_LONG).show()
        }

        binding.savePost.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()

            if (firebaseAuth.currentUser != null) {
                val email = firebaseAuth.currentUser!!.email.toString()
                val descricao = binding.descricao.text.toString()
                val imageString = Base64Converter.drawableToString(
                    binding.picturePost.drawable
                )
                val db = Firebase.firestore
                val dados = hashMapOf(
                    "descricao" to descricao,
                    "imageString" to imageString
                )
                db.collection("posts").document(email)
                    .set(dados)
                    .addOnSuccessListener {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
            }
        }
    }
}