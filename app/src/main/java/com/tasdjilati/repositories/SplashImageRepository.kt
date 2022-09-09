package com.tasdjilati.repositories

import androidx.lifecycle.LiveData
import com.tasdjilati.data.dao.SplashImageDao
import com.tasdjilati.data.entities.SplashImage

class SplashImageRepository (private val splashImageDao: SplashImageDao) {

    val getImage: LiveData<List<SplashImage>> = splashImageDao.getImage()

    suspend fun addImage(image : SplashImage){
        splashImageDao.insertImage(image)
    }

    suspend fun updateName(name : String){
        return splashImageDao.updateName(name)
    }
}