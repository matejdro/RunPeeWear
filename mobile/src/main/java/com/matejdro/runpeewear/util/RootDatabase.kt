package com.matejdro.runpeewear.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.topjohnwu.superuser.io.SuFileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import java.io.File

abstract class RootDatabase(private val context: Context) {
   private var _database: SQLiteDatabase? = null

   val database: SQLiteDatabase
      get() = _database ?: throw IllegalStateException("Database not loaded. Call ensureDatabaseIsLoaded() first")

   protected abstract val originalFile: File

   suspend fun ensureDatabaseIsLoaded() {
      if (_database == null) {
         reload()
      }
   }

   suspend fun reload() {
      val file = withContext(Dispatchers.IO) {
         val targetFile = File(context.cacheDir, originalFile.name)

         SuFileInputStream.open(originalFile).source().buffer().use { originalFileStream ->
            targetFile.sink().buffer().use { targetFileStream ->
               targetFileStream.writeAll(originalFileStream)
            }
         }

         targetFile
      }

      _database = SQLiteDatabase.openDatabase(file.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
   }
}
