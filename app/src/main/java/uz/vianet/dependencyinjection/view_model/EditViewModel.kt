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
class EditViewModel @Inject constructor(val postService: PostService) : ViewModel(),CoroutineScope {
    var job: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job!!
    init {
        job = Job()
    }
    val editedPost = MutableLiveData<Post>()
    val singlePost = MutableLiveData<Post>()
    val error = MutableLiveData<Throwable>()
    val handler = CoroutineExceptionHandler { _, exception ->
        error.value = exception
    }


    fun apiLoadPost(id: Int) {
        Log.d("@@EditViewModel","$id Post Came To ViewModel")
        launch(Dispatchers.Main + handler) {
            singlePost.value = getPost(id)
        }
    }
    private suspend fun getPost(id:Int):Post{
        return async(Dispatchers.IO) {
            val response = postService.detailPost(id).execute()
            Log.d("@@EditViewModel","${response.body()!!.title} Post Responded")
            return@async response.body()!!
        }.await()
    }

    fun apiPostUpdate(post: Post) {
        Log.d("@@EditViewModel","${post.title} Post Came To ViewModel")
        launch(Dispatchers.Main + handler) {
            editedPost.value = updatedPost(post)
        }
    }
    private suspend fun updatedPost(post: Post):Post{
        return async(Dispatchers.IO) {
            val response = postService.updatePost(post.id,post).execute()
            Log.d("@@EditViewModel","${response.body()!!.title} Post Responded")
            return@async response.body()!!
        }.await()
    }
    fun cancelHandleData() {
        job?.cancel()
        job = null
    }
}