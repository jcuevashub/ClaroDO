package com.example.contactsapp.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.contactsapp.R

@Composable
fun ContactQuickActions(
    phoneNumber: String,
    onCallClick: (String) -> Unit = {},
    onMessageClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledTonalIconButton(
            onClick = {
                onCallClick(phoneNumber)
                try {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phoneNumber")
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                }
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Call,
                contentDescription = "Llamar",
                modifier = Modifier.size(16.dp)
            )
        }

        FilledTonalIconButton(
            onClick = {
                onMessageClick(phoneNumber)
                try {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("sms:$phoneNumber")
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                }
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Email,
                contentDescription = "Mensaje",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}