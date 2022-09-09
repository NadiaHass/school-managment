package com.tasdjilati.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.tasdjilati.data.StudentsDatabase
import com.tasdjilati.data.entities.SplashImage
import com.tasdjilati.repositories.SplashImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashImageViewModel (application: Application) : AndroidViewModel(application) {
    val getImage : LiveData<List<SplashImage>>
    private val repository : SplashImageRepository

    init {
        val imageDao = StudentsDatabase.getDatabase(application).getImageDao()
        repository = SplashImageRepository((imageDao))
        getImage = repository.getImage
    }

    fun addImage(image : SplashImage){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addImage(image)
        }
    }

    fun updateName(name : String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateName(name)
        }
    }
}