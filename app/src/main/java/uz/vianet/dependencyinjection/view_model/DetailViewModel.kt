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
class DetailViewModel @Inject constructor(val postService: PostService) : ViewModel(),CoroutineScope{

    var job: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job!!
    init {
        job = Job()
    }
    val error = MutableLiveData<Throwable>()
    val handler = CoroutineExceptionHandler { _, exception ->
        error.value = exception
    }
    val detailPost = MutableLiveData<Post>()

    fun apiPostDetail(id: Int) {
        Log.d("@@DetailViewModel","$id Post Came To ViewModel")
        launch(Dispatchers.Main + handler) {
            detailPost.value = getPost(id)
        }
    }
    private suspend fun getPost(id:Int):Post{
        return async(Dispatchers.IO) {
            val response = postService.detailPost(id).execute()
            Log.d("@@DetailViewModel","${response.body()!!.title} Post Responded")
            return@async response.body()!!
        }.await()
    }
    fun cancelHandleData() {
        job?.cancel()
        job = null
    }
}