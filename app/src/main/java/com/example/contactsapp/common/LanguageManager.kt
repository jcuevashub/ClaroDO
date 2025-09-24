package com.example.contactsapp.common

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "language_preferences")

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val languageKey = stringPreferencesKey("selected_language")

    val currentLanguage: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[languageKey] ?: getSystemLanguage()
    }

    suspend fun setLanguage(languageCode: String) {

        context.dataStore.edit { preferences ->
            preferences[languageKey] = languageCode
        }

        val prefs = context.getSharedPreferences("language_temp", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_language", languageCode).apply()
    }

    private fun getSystemLanguage(): String {
        val systemLocale = Locale.getDefault()
        return when (systemLocale.language) {
            StringConstants.LANG_SPANISH -> StringConstants.LANG_SPANISH
            else -> StringConstants.LANG_ENGLISH
        }
    }

    fun applyLanguage(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        val config = Configuration(context.resources.configuration)

        config.setLocale(locale)
        config.setLocales(LocaleList(locale))

        return context.createConfigurationContext(config)
    }

    fun getAvailableLanguages(): List<LanguageOption> {
        return listOf(
            LanguageOption(
                code = StringConstants.LANG_ENGLISH,
                name = "English",
                nativeName = "English"
            ),
            LanguageOption(
                code = StringConstants.LANG_SPANISH,
                name = "Spanish",
                nativeName = "Español"
            )
        )
    }
}

data class LanguageOption(
    val code: String,
    val name: String,
    val nativeName: String
)