package com.matejdro.runpeewear.data

import android.annotation.SuppressLint
import android.content.Context
import com.matejdro.runpeewear.model.Movie
import com.matejdro.runpeewear.util.RootDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalStdlibApi::class)
@Singleton
class MovieDatabase @Inject constructor(@ApplicationContext context: Context) : RootDatabase(context) {
   suspend fun loadMovies(): List<Movie> = withContext(Dispatchers.IO) {
      ensureDatabaseIsLoaded()

      buildList {
         database.query(
            /* table = */ "movies",
            /* columns = */ arrayOf("mKey", "title"),
            /* selection = */ null,
            /* selectionArgs = */ null,
            /* groupBy = */ null,
            /* having = */ null,
            /* orderBy = */ "title COLLATE NOCASE ASC"
         ).use { cursor ->
            while (cursor.moveToNext()) {
               val id = cursor.getInt(0)
               val title = cursor.getString(1)

               add(Movie(id, title))
            }
         }
      }
   }

   override val originalFile: File
      @SuppressLint("SdCardPath")
      get() = File("/data/data/air.RunPee/databases/movies1.db")
}
