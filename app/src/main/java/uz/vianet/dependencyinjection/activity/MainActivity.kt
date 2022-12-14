package uz.vianet.dependencyinjection.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import uz.vianet.dependencyinjection.adapter.PostAdapter
import uz.vianet.dependencyinjection.databinding.ActivityMainBinding
import uz.vianet.dependencyinjection.model.Post
import uz.vianet.dependencyinjection.utils.Utils
import uz.vianet.dependencyinjection.utils.Utils.toast
import uz.vianet.dependencyinjection.view_model.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        viewModel.apiPostList()
        viewModel.allPosts.observe(this) {
            refreshAdapter(it)
        }

        binding.floating.setOnClickListener { openCreateActivity() }

        val extras = intent.extras
        if (extras != null) {
            binding.pbLoading.visibility = View.VISIBLE
            Toast.makeText(this@MainActivity,"${extras.getString("title")} Updated", Toast.LENGTH_LONG).show()
            viewModel.apiPostList()
        }
    }

    private fun refreshAdapter(posts: ArrayList<Post>) {
        val adapter = PostAdapter(this, posts)
        binding.recyclerView.setAdapter(adapter)
        binding.pbLoading.visibility = View.GONE
    }
    fun openCreateActivity() {
        val intent = Intent(this@MainActivity, CreateActivity::class.java)
        launchCreateActivity.launch(intent)
    }

    var launchCreateActivity = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.pbLoading.visibility = View.VISIBLE
            if (result.data != null) {
                val title = result.data!!.getStringExtra("title")
                toast(this@MainActivity,"$title Created")
                viewModel.apiPostList()
            }
        } else {
            Toast.makeText(this@MainActivity, "Operation canceled", Toast.LENGTH_LONG).show()
        }
    }
    fun deletePostDialog(post: Post) {
        val title = "Delete"
        val body = "Do you want to delete?"
        Utils.customDialog(this@MainActivity, title, body, object : Utils.DialogListener {
            override fun onPositiveClick() {
                Log.d("Delete Post","Post - ${post.id} sent to delete")
                viewModel.apiPostDelete(post)
                viewModel.deletedPost.observe(this@MainActivity) {
                    viewModel.apiPostList()


                }
                toast(this@MainActivity,"${post.title} deleted.")
            }

            override fun onNegativeClick() {}
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelHandleData()
    }
}