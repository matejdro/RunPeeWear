package com.matejdro.runpeewear.data

import android.annotation.SuppressLint
import android.content.Context
import com.matejdro.runpeewear.model.PeeTime
import com.matejdro.runpeewear.util.RootDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalStdlibApi::class)
@Singleton
class PeetimesDatabase @Inject constructor(@ApplicationContext context: Context) : RootDatabase(context) {
   suspend fun loadPeetimes(movieId: Int): List<PeeTime> = withContext(Dispatchers.IO) {
      ensureDatabaseIsLoaded()

      buildList {
         database.query(
            /* table = */ "peetimes",
            /* columns = */ arrayOf("time", "timeSec", "recommended", "cue", "synopsis"),
            /* selection = */ "mKey = ? AND time > 1 AND isMeta = 0",
            /* selectionArgs = */ arrayOf(movieId.toString()),
            /* groupBy = */ null,
            /* having = */ null,
            /* orderBy = */ "time ASC, timeSec ASC"
         ).use { cursor ->
            while (cursor.moveToNext()) {
               val timeMinutes = cursor.getInt(0)
               val timeSeconds = cursor.getInt(1)
               val recommended = cursor.getInt(2)
               val cue = cursor.getString(3)
               val synopsis = cursor.getString(4)

               var totalTimeSeconds = timeMinutes * 60 + timeSeconds

               val isCreditsCue = recommended > 1
               if (!isCreditsCue) {
                  // Alert 30 seconds before actual cue
                  totalTimeSeconds -= 30
               }

               add(
                  PeeTime(totalTimeSeconds, cue, synopsis, recommended > 0)
               )
            }
         }
      }
   }

   override val originalFile: File
      @SuppressLint("SdCardPath")
      get() = File("/data/data/air.RunPee/databases/peetimes.db")
}
