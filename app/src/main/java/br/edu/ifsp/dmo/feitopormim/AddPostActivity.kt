package br.edu.ifsp.dmo.feitopormim

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.edu.ifsp.dmo.feitopormim.databinding.ActivityAddPostBinding
import br.edu.ifsp.dmo.feitopormim.util.Base64Converter
import br.edu.ifsp.dmo.feitopormim.util.LocalizacaoHelper
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AddPostActivity : AppCompatActivity(), LocalizacaoHelper.Callback {

    private lateinit var binding: ActivityAddPostBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()

        binding.close.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

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
        }

        binding.getLocation.setOnClickListener {
            solicitarLocalizacao()
        }

        binding.savePost.setOnClickListener {
            if(firebaseAuth.currentUser != null){
                val fotoPostString = if (binding.picturePost.drawable != null){
                    Base64Converter.drawableToString(binding.picturePost.drawable)
                }else{
                    Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }

                val data_criacao = Timestamp.now()

                val localizacao = binding.location.text.toString() + "  📍"

                val descricao = binding.txtDescricao.text.toString()

                if(descricao == null){
                    Toast.makeText(this, "Nenhuma descrição!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }

                val db = Firebase.firestore

                val dados = hashMapOf(
                    "imageString" to fotoPostString,
                    "descricao" to descricao,
                    "dataCriacao" to data_criacao,
                    "localizacao" to localizacao
                )

                db.collection("posts").document()
                    .set(dados)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Post feito com sucesso!", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun solicitarLocalizacao() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            val localizacaoHelper = LocalizacaoHelper(applicationContext)
            localizacaoHelper.obterLocalizacaoAtual(this)
        }
    }

    override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double)
    {
        runOnUiThread {
            var infos = endereco.subAdminArea + " - "
            infos += endereco.adminArea
            binding.location.text = infos
        }
    }
    override fun onErro(mensagem: String) {
        System.out.println(mensagem)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions,
            grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            solicitarLocalizacao()
        } else {
            Toast.makeText(this, "Permissão de localização negada",
                Toast.LENGTH_SHORT).show()
        }
    }
}