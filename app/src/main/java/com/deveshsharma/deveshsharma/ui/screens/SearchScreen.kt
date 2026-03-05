package com.deveshsharma.deveshsharma.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.deveshsharma.deveshsharma.data.model.NewsArticle

@Composable
fun SearchScreen(news: List<NewsArticle>) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val matchingNews = if (searchQuery.isBlank()) {
        emptyList()
    } else {
        news.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    (it.description?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(10.dp).padding(top = 40.dp).padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(percent = 30))
                        .padding(16.dp)
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 24.dp)
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Enter Something...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }
            }
        )

        if (searchQuery.isNotBlank()) {
            if (matchingNews.isNotEmpty()) {
                LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                    items(matchingNews) { article ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { article.link.let { Intent(Intent.ACTION_VIEW,
                                    it.toUri()).also(context::startActivity) } }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (article.image_url != null) {
                                    AsyncImage(
                                        model = article.image_url,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(MaterialTheme.shapes.medium),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                Text(
                                    text = article.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                article.description?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    "No results found",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
