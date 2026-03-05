package com.deveshsharma.deveshsharma.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.deveshsharma.deveshsharma.data.model.NewsArticle
import com.deveshsharma.deveshsharma.ui.theme.DeveshSharmaTheme

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val news = intent.getParcelableArrayListExtra<NewsArticle>("news")
        setContent {
            DeveshSharmaTheme {
                if (news != null) {
                    SearchScreen(news = news)
                }
            }
        }
    }
}
