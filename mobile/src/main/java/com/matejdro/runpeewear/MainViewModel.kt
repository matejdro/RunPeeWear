package com.matejdro.runpeewear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matejdro.runpeewear.data.MovieDatabase
import com.matejdro.runpeewear.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val movieDatabase: MovieDatabase) : ViewModel() {
   private val _movies = MutableStateFlow<List<Movie>>(emptyList())
   val movies: StateFlow<List<Movie>>
      get() = _movies

   private var loadingJob: Job? = null

   init {
      loadingJob = viewModelScope.launch {
         _movies.value = movieDatabase.loadMovies()
      }
   }

   fun search(keyword: String) {
      loadingJob?.cancel()
      loadingJob = viewModelScope.launch {
         _movies.value = movieDatabase.loadMovies(keyword)
      }
   }
}
