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
class CreateViewModel @Inject constructor(val postService: PostService) : ViewModel(),CoroutineScope {

    var job: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job!!
    init {
        job = Job()
    }
    val newPost = MutableLiveData<Post>()
    val error = MutableLiveData<Throwable>()
    val handler = CoroutineExceptionHandler { _, exception ->
        error.value = exception
    }
     fun apiPostCreate(post: Post) {
        launch(Dispatchers.Main + handler) {
            newPost.value = createNewPost(post)
        }
    }
    private suspend fun createNewPost(post: Post):Post{
        return async(Dispatchers.IO) {
            val response = postService.createPost(post).execute()
            return@async response.body()!!
        }.await()
    }

    fun cancelHandleData() {
        job?.cancel()
        job = null
    }
}