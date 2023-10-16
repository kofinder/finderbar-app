package com.finderbar.jovian.viewmodels.post

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.MutableLiveData
import com.finderbar.jovian.models.Movie


class MovieDetailVM: ViewModel() {
    val movie: MutableLiveData<Movie>? = null

    fun getMovie(mv: Movie): LiveData<Movie> {
        movie?.postValue(mv);
        return movie as MutableLiveData<Movie>
    }

}