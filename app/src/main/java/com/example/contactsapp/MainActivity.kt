package com.example.contactsapp

import android.content.Context
import android.os.Bundle
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
import com.example.contactsapp.presentation.navigation.NavGraph
import com.example.contactsapp.ui.theme.ContactsAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var languageManager: LanguageManager

    override fun attachBaseContext(newBase: Context) {
        // Apply language before activity creation
        super.attachBaseContext(newBase)
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

                    // Apply language context
                    LaunchedEffect(currentLanguage) {
                        // Language change will be handled by recreating activity
                    }

                    NavGraph(
                        navController = navController,
                        onLanguageChanged = {
                            recreate() // Recreate activity to apply language changes
                        }
                    )
                }
            }
        }
    }
}