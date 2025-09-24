package com.example.contactsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.contactsapp.presentation.contactlist.ContactListScreen
import com.example.contactsapp.presentation.createcontact.CreateContactScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.CONTACT_LIST
    ) {
        composable(Routes.CONTACT_LIST) {
            ContactListScreen(
                onNavigateToCreateContact = {
                    navController.navigate(Routes.CREATE_CONTACT)
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
    }
}