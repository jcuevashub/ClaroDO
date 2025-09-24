package com.example.contactsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.contactsapp.presentation.contactlist.ContactListScreenEnhanced
import com.example.contactsapp.presentation.createcontact.CreateContactScreen
import com.example.contactsapp.presentation.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    onLanguageChanged: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Routes.CONTACT_LIST
    ) {
        composable(Routes.CONTACT_LIST) {
            ContactListScreenEnhanced(
                onNavigateToCreateContact = {
                    navController.navigate(Routes.CREATE_CONTACT)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }
        composable(Routes.CREATE_CONTACT) {
            CreateContactScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLanguageChanged = onLanguageChanged
            )
        }
    }
}