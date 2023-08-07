package com.gjwork.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gjwork.dogs.model.DogBreed

class DetailViewModel: ViewModel() {

    val dogLiveData = MutableLiveData<DogBreed>()

    fun fetch() {
        val dog = DogBreed("3", "Rotwailer", "20 years", "breedGroup", "bredFor", "temperament", "")
        dogLiveData.value = dog
    }
}