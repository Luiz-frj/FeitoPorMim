package br.edu.ifsp.dmo.feitopormim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.dmo.feitopormim.adapter.PostAdapter
import br.edu.ifsp.dmo.feitopormim.databinding.ActivityHomeBinding
import br.edu.ifsp.dmo.feitopormim.model.Post
import br.edu.ifsp.dmo.feitopormim.util.Base64Converter
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class HomeActivity: AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var ultimoTimestamp: Timestamp? = null
    private val posts = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()

        carregarMaisPosts()

        binding.goToPerfil.setOnClickListener {
            startActivity(Intent(this, EditarPerfilActivity::class.java))
            finish()
        }

        binding.buttonExit.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.goFeed.setOnClickListener {
            carregarMaisPosts()
        }

        binding.createPost.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
            finish()
        }

        binding.limparPesquisa.setOnClickListener {
            binding.searchLocationEditText.setText("")
            posts.clear()
            ultimoTimestamp = null
            carregarMaisPosts()
        }

        binding.searchButton.setOnClickListener {
            val localizacao = binding.searchLocationEditText.text.toString()

            if (localizacao.isNotEmpty()) {
                pesquisarPorLocalizacao(localizacao)
            } else {
                Toast.makeText(this, "Por favor, insira uma localização para pesquisa", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun carregarMaisPosts() {
        val db = Firebase.firestore
        val postsRef = db.collection("posts")
            .orderBy("data_criacao", Query.Direction.DESCENDING)
            .limit(5)

        val query = if (ultimoTimestamp != null) {
            postsRef.whereLessThan("data_criacao", ultimoTimestamp!!)
        } else {
            postsRef
        }

        query.get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    for (document in result.documents) {
                        val imageString = document.getString("imageString") ?: continue
                        val descricao = document.getString("descricao") ?: ""
                        val localizacao = document.getString("localizacao") ?: ""
                        val bitmap = Base64Converter.stringToBitmap(imageString)

                        posts.add(Post(descricao, bitmap, localizacao))
                    }

                    ultimoTimestamp = result.documents.last().getTimestamp("data_criacao")

                    val adapter = PostAdapter(posts.toTypedArray())
                    binding.recycleView.layoutManager = LinearLayoutManager(this)
                    binding.recycleView.adapter = adapter
                } else {
                    Toast.makeText(this, "Nenhum post restante", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar posts", Toast.LENGTH_SHORT).show()
            }
    }

    private fun pesquisarPorLocalizacao(localizacao: String) {
        val db = Firebase.firestore
        val postsRef = db.collection("posts")
            .whereGreaterThanOrEqualTo("localizacao", localizacao)

        postsRef.get()
            .addOnSuccessListener { result ->
                posts.clear()

                if (!result.isEmpty) {
                    for (document in result.documents) {
                        val imageString = document.getString("imageString") ?: continue
                        val descricao = document.getString("descricao") ?: ""
                        val localizacao = document.getString("localizacao") ?: ""
                        val bitmap = Base64Converter.stringToBitmap(imageString)

                        posts.add(Post(descricao, bitmap, localizacao))
                    }

                    val adapter = PostAdapter(posts.toTypedArray())
                    binding.recycleView.layoutManager = LinearLayoutManager(this)
                    binding.recycleView.adapter = adapter
                } else {
                    Toast.makeText(this, "Nenhum post encontrado para essa localização", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao realizar a pesquisa", Toast.LENGTH_SHORT).show()
            }
    }
}