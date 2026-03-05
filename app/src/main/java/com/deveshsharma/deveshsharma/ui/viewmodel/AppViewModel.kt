package com.deveshsharma.deveshsharma.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deveshsharma.deveshsharma.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val _appList = MutableStateFlow<List<AppInfo>>(emptyList())
    val appList: StateFlow<List<AppInfo>> = _appList.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            val loadedApps = withContext(Dispatchers.Default) {
                val pm = getApplication<Application>().packageManager
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
            _appList.value = loadedApps
        }
    }
}
