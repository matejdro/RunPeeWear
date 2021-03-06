package com.matejdro.runpeewear

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.matejdro.runpeewear.ui.MobileAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
   private val viewModel: MainViewModel by viewModels()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      lifecycleScope.launch {
         viewModel.selectionResult.collect {
            val text = when (it) {
               is MovieSelectionResult.Failure -> {
                  it.e.printStackTrace()
                  "Failed to transfer peetimes to the watch: ${it.e.message}"
               }
               MovieSelectionResult.Success -> "Transferred peetimes to the watch"
            }

            Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
         }
      }

      setContent {
         MobileAppTheme {
            Surface {
               ComposeContent()
            }
         }
      }
   }

   override fun onStart() {
      super.onStart()

      viewModel.reload()
   }

   @Composable
   private fun ComposeContent() {
      Column {
         SearchBox()

         MovieList(
            Modifier
               .fillMaxWidth()
               .weight(1f)
         )
      }
   }

   @Composable
   private fun SearchBox() {
      var value by remember {
         mutableStateOf(TextFieldValue())
      }

      TextField(
         value,
         onValueChange = {
            value = it
            viewModel.search(it.text)
         },
         placeholder = {
            Text("Search")
         },
         modifier = Modifier.fillMaxWidth()
      )
   }

   @Composable
   private fun MovieList(modifier: Modifier) {
      val movies by viewModel.movies.collectAsState()

      LazyColumn(modifier, contentPadding = PaddingValues(vertical = 16.dp)) {
         items(movies) {
            Text(it.title,
               Modifier
                  .fillMaxWidth()
                  .clickable {
                     viewModel.onMovieSelected(it)
                  }
                  .padding(32.dp), fontSize = 18.sp)
            Divider()
         }
      }
   }
}
