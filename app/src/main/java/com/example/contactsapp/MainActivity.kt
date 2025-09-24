package com.example.contactsapp

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.contactsapp.common.LanguageManager
import com.example.contactsapp.common.StringConstants
import com.example.contactsapp.presentation.navigation.NavGraph
import com.example.contactsapp.ui.theme.ContactsAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var languageManager: LanguageManager

    override fun attachBaseContext(newBase: Context) {

        val prefs: SharedPreferences = newBase.getSharedPreferences("language_temp", Context.MODE_PRIVATE)
        val savedLanguage = prefs.getString("selected_language", getSystemLanguage()) ?: getSystemLanguage()
        val contextWithLanguage = applyLanguageContext(newBase, savedLanguage)
        super.attachBaseContext(contextWithLanguage)
    }

    private fun getSystemLanguage(): String {
        val systemLocale = Locale.getDefault()
        return when (systemLocale.language) {
            StringConstants.LANG_SPANISH -> StringConstants.LANG_SPANISH
            else -> StringConstants.LANG_ENGLISH
        }
    }

    private fun applyLanguageContext(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        val config = Configuration(context.resources.configuration)

        config.setLocale(locale)
        config.setLocales(LocaleList(locale))

        return context.createConfigurationContext(config)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContactsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val currentLanguage by languageManager.currentLanguage.collectAsStateWithLifecycle(
                        initialValue = "es"
                    )

                    LaunchedEffect(currentLanguage) {

                    }

                    NavGraph(
                        navController = navController,
                        onLanguageChanged = {
                            recreate()
                        }
                    )
                }
            }
        }
    }
}