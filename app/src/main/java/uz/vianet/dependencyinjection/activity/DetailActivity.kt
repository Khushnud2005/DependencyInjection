package uz.vianet.dependencyinjection.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.vianet.dependencyinjection.databinding.ActivityDetailBinding
import uz.vianet.dependencyinjection.utils.Utils.toast
import uz.vianet.dependencyinjection.view_model.DetailViewModel

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    private val viewModel:DetailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        val extras = intent.extras
        if (extras != null) {
            Log.d("###", "extras not NULL - ")
            val id = extras.getInt("id")
            viewModel.apiPostDetail(id)
            viewModel.detailPost.observe(this){
                binding.tvTitle.setText(it.title.uppercase())
                binding.tvBody.setText(it.body)
            }
            viewModel.error.observe(this){
                toast(this,it.message.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelHandleData()
    }
}