package com.example.modernakotlinprojektet

import androidx.lifecycle.ViewModel

class CountingViewModel : ViewModel() {

    var count = 0
    fun incCount(){
        count++
    }

    fun decCount(){
        count--
    }
}