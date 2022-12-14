package uz.vianet.dependencyinjection.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import uz.vianet.dependencyinjection.model.Post
import uz.vianet.dependencyinjection.network.service.PostService
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class MainViewModel @Inject constructor(val postService: PostService) : ViewModel(),CoroutineScope {
     var job: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job!!
    init {
        job = Job()
    }
    val allPosts = MutableLiveData<ArrayList<Post>>()
    val deletedPost = MutableLiveData<Post>()
    val error = MutableLiveData<Throwable>()
    val handler = CoroutineExceptionHandler { _, exception ->
        error.value = exception
    }
    private suspend fun getPosts():ArrayList<Post>{
        return async(Dispatchers.IO) {
            val response = postService.listPost().execute()
            return@async response.body()!!
        }.await()
    }
    fun apiPostList() {
        launch (Dispatchers.Main + handler) {
            val posts = getPosts()
            allPosts.value = posts
        }

    }

    fun apiPostDelete(post: Post){
        launch(Dispatchers.Main + handler) {
            Log.d("Delete Post","Post - ${post.id} come to viewModel")
            var delPost = deletePost(post)
            deletedPost.value = delPost
        }
    }

    private suspend fun deletePost(post: Post): Post {
        return async(Dispatchers.IO) {
            val response = postService.deletePost(post.id).execute()
            Log.d("Delete Post","Post - ${post.id} deleted")
            return@async response.body()!!

        }.await()
    }

    fun cancelHandleData() {
        job?.cancel()
        job = null
    }
}