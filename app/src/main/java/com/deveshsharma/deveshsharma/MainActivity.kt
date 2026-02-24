package com.deveshsharma.deveshsharma

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.deveshsharma.deveshsharma.ui.screens.InfoScreen
import com.deveshsharma.deveshsharma.ui.screens.TasksScreen
import com.deveshsharma.deveshsharma.ui.theme.DeveshSharmaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeveshSharmaTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    val pagerState = rememberPagerState(initialPage = 1) { 3 }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        scope.launch {
            pagerState.scrollToPage(1)
        }
    }

    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> InfoScreen()
            1 -> AppLauncher()
            2 -> TasksScreen()
        }
    }
}

data class AppInfo(val label: String, val packageName: String, val icon: Drawable)

@Composable
fun TypingText(texts: List<String>, modifier: Modifier = Modifier) {
    var displayedText by rememberSaveable { mutableStateOf("") }
    var isPaused by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                isPaused = true
                Log.d("Lifecycle", "PAUSED")
            } else if (event == Lifecycle.Event.ON_RESUME) {
                isPaused = false
                Log.d("Lifecycle", "RESUMED")
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(key1 = texts, key2 = isPaused) {
        if (!isPaused) {
            var i = 0
            while (true) {
                displayedText = ""
                texts[i].forEach { char ->
                    delay(100)
                    displayedText += char
                }
                i = (i + 1) % texts.size
                delay(1500)
                Log.d("TypingEffect", "Running.....")
            }
        }
    }

    Text(
        text = buildAnnotatedString {
            val lastSpaceIndex = displayedText.lastIndexOf(' ')
            if (lastSpaceIndex != -1) {
                append(displayedText.take(lastSpaceIndex + 1))
                withStyle(style = SpanStyle(color = Color.Blue)) {
                    append(displayedText.substring(lastSpaceIndex + 1))
                }
            } else {
                withStyle(style = SpanStyle(color = Color.Blue)) {
                    append(displayedText)
                }
            }
        },
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium
    )
}


@Composable
fun AppLauncher() {
    val context = LocalContext.current
    val packageManager = context.packageManager
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val appList = remember {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).also {
            it.addCategory(Intent.CATEGORY_LAUNCHER)
        }
        pm.queryIntentActivities(mainIntent, 0).mapNotNull { resolveInfo ->
            try {
                val applicationInfo = pm.getApplicationInfo(resolveInfo.activityInfo.packageName, 0)
                AppInfo(
                    label = pm.getApplicationLabel(applicationInfo).toString(),
                    packageName = applicationInfo.packageName,
                    icon = pm.getApplicationIcon(applicationInfo)
                )
            } catch (_: PackageManager.NameNotFoundException) {
                null
            }
        }.distinctBy { it.packageName }.sortedBy { it.label.lowercase() }
    }

    val homeScreenAppPackages = remember {
        listOf("com.whatsapp", "com.android.chrome", "com.microsoft.copilot", "com.google.android.youtube")
    }
    val homeScreenApps = remember(appList) {
        appList.filter { it.packageName in homeScreenAppPackages }
    }

    val displayedAppList = if (searchQuery.text.isBlank()) {
        homeScreenApps
    } else {
        appList.filter { it.label.contains(searchQuery.text, ignoreCase = true) }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TypingText(
            texts = listOf("Hello, Devesh", "Welcome Back, Devesh", "Hello, Master"),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search apps or Copilot") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    val topSuggestion = displayedAppList.firstOrNull()
                    if (topSuggestion != null) {
                        val launchIntent = packageManager.getLaunchIntentForPackage(topSuggestion.packageName)
                        context.startActivity(launchIntent)
                    } else if (searchQuery.text.isNotEmpty()) {
                        val encodedQuery = Uri.encode(searchQuery.text)
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://www.bing.com/search?q=$encodedQuery".toUri()
                            )
                            intent.setPackage("com.microsoft.copilot")
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("Devesh", e.toString())
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://www.bing.com/search?q=$encodedQuery".toUri()
                            )
                            context.startActivity(intent)
                        }
                    }
                }
            )
        )

        if (displayedAppList.isEmpty() && searchQuery.text.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No matching apps found.",
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.padding(16.dp)
            ) {
                items(displayedAppList) { app ->
                    AppIcon(app) {
                        val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
                        context.startActivity(launchIntent)
                    }
                }
            }
        }
    }
}

@Composable
fun AppIcon(app: AppInfo, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            bitmap = app.icon.toBitmap().asImageBitmap(),
            contentDescription = app.label,
            modifier = Modifier.size(40.dp)
        )
        Text(text = app.label, fontSize = 10.sp, maxLines = 2, modifier = Modifier.padding(top = 4.dp))
    }
}
