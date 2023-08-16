package com.matejdro.runpeewear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataRequest
import com.matejdro.runpeewear.common.CommPaths
import com.matejdro.runpeewear.data.MovieDatabase
import com.matejdro.runpeewear.data.PeetimesDatabase
import com.matejdro.runpeewear.model.Movie
import com.matejdro.runpeewear.model.PeeTime
import com.matejdro.runpeewear.model.PeeTimes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val movieDatabase: MovieDatabase,
    private val peetimesDatabase: PeetimesDatabase,
    private val dataClient: DataClient
) : ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>>
        get() = _movies

    private val _selectionResult = MutableSharedFlow<MovieSelectionResult>(extraBufferCapacity = 1)
    val selectionResult: SharedFlow<MovieSelectionResult>
        get() = _selectionResult

    private var loadingJob: Job? = null

    init {
        loadingJob = viewModelScope.launch {
            _movies.value = movieDatabase.loadMovies()
        }
    }

    fun reload() {
        viewModelScope.launch {
            movieDatabase.reload()
            peetimesDatabase.reload()
        }
    }

    fun search(keyword: String) {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            _movies.value = movieDatabase.loadMovies(keyword)
        }
    }

    fun onMovieSelected(movie: Movie) = viewModelScope.launch {
        try {
            val peetimeList = peetimesDatabase.loadPeetimes(movie.id)
            if (peetimeList.isEmpty()) {
                throw IllegalStateException("No peetimes. Open movie in the PeeTime app first.")
            }

            // Add fake peetime to the end of the movie to not spoil that the movie is ending
            val finalFakePeetime = peetimeList.last().let { lastPeetime ->
                PeeTime(
                    cueTimeSecondsAfterMovieStart = lastPeetime.cueTimeSecondsAfterMovieStart + 600,
                    cue = "END",
                    synopsis = "END"
                )
            }

            val peeTimes = PeeTimes(movie.title, movie.timerCue, peetimeList + finalFakePeetime)

            dataClient.putDataItem(
                PutDataRequest.create(CommPaths.DATA_PEE_TIMES)
                    .setData(peeTimes.encode())
            ).await()

            _selectionResult.emit(MovieSelectionResult.Success)
        } catch (e: Exception) {
            _selectionResult.tryEmit(MovieSelectionResult.Failure(e))
        }
    }
}

sealed interface MovieSelectionResult {
    object Success : MovieSelectionResult
    class Failure(val e: Exception) : MovieSelectionResult
}
