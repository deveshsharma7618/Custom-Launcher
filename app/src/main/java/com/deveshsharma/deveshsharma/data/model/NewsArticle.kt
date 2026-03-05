
package com.deveshsharma.deveshsharma.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsArticle(
    val title: String,
    val link: String,
    val description: String?,
    val content: String?,
    val image_url: String?
) : Parcelable
