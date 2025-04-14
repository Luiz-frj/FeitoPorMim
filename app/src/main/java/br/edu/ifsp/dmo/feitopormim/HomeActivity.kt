package br.edu.ifsp.dmo.feitopormim

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.dmo.feitopormim.adapter.PostAdapter
import br.edu.ifsp.dmo.feitopormim.databinding.ActivityHomeBinding
import br.edu.ifsp.dmo.feitopormim.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class HomeActivity: AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding
    private lateinit var adapter: PostAdapter;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()


        binding.buttonExit.setOnClickListener {
            firebaseAuth.signOut();
            startActivity(Intent(this, MainActivity::class.java))
            finish();
        }

        binding.goFeed.setOnClickListener {
            val db = Firebase.firestore
            db.collection("posts").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val document = task.result
                        val posts = ArrayList<Post>()
                        for (document in document.documents) {
                            val imageString = document.data!!["imageString"].toString()
                            val bitmap = Base64Converter.stringToBitmap(imageString)
                            val descricao = document.data!!["descricao"].toString()
                            posts.add(Post(descricao, bitmap))
                        }
                        adapter = PostAdapter(posts.toTypedArray())
                        binding.recycleView.layoutManager = LinearLayoutManager(this)
                        binding.recycleView.adapter = adapter
                    }
                }
        }
    }
}