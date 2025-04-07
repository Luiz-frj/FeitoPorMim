package br.edu.ifsp.dmo.feitopormim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo.feitopormim.databinding.ActivityProfileBinding

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
                binding.Profileimage.setImageURI(uri)
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
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            Toast.makeText(this, "Usuario e foto salvos", Toast.LENGTH_LONG).show()
        }
    }

}