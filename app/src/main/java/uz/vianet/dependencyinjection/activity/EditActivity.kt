package uz.vianet.dependencyinjection.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.vianet.dependencyinjection.databinding.ActivityEditBinding
import uz.vianet.dependencyinjection.model.Post
import uz.vianet.dependencyinjection.utils.Utils.toast
import uz.vianet.dependencyinjection.view_model.EditViewModel

@AndroidEntryPoint
class EditActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditBinding
    private val viewModel:EditViewModel by viewModels()
    var id = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        val extras = intent.extras
        if (extras != null) {
            Log.d("###", "extras not NULL - ")
            id = extras.getInt("id")
            loadPost(id)
        }
        binding.btnSubmit.setOnClickListener {
            updatePost()
        }
    }

    private fun updatePost() {
        val title = binding.etTitle.text.toString()
        val body = binding.etText.text.toString().trim { it <= ' ' }
        val id_user = binding.etUserId.text.toString().trim { it <= ' ' }

        if (title.isNotEmpty() && body.isNotEmpty() && id_user.isNotEmpty()){
            val post = Post(id.toInt(),id_user.toInt(), title, body)
            Log.d("@@EditActivity","Pot model Created")
            viewModel.apiPostUpdate(post)
            viewModel.editedPost.observe(this){
                Log.d("@@EditActivity","${it.title} Post Edited")
                val intent = Intent(this@EditActivity, MainActivity::class.java)
                intent.putExtra("title", it.title)
                startActivity(intent)
            }
            viewModel.error.observe(this){
                toast(this,it.message.toString())
            }
        }
    }

    private fun loadPost(id: Int) {
        viewModel.apiLoadPost(id)
        viewModel.singlePost.observe(this){
            binding.etUserId.setText(it.userId.toString())
            binding.etTitle.setText(it.title.toUpperCase())
            binding.etText.setText(it.body)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelHandleData()
    }
}