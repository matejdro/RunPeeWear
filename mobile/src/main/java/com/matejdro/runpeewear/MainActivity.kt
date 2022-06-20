package com.matejdro.runpeewear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matejdro.runpeewear.ui.MobileAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileAppTheme {
                Surface {
                    ComposeContent()
                }
            }
        }
    }

    @Composable
    private fun ComposeContent() {
        MovieList(Modifier.fillMaxSize())
    }

    @Composable
    private fun MovieList(modifier: Modifier) {
        val movies by viewModel.movies.collectAsState()

        LazyColumn(modifier, contentPadding = PaddingValues(vertical = 16.dp)) {
            items(movies) {
                Text(it.title,
                    Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(32.dp), fontSize = 18.sp)
                Divider()
            }
        }
    }
}
