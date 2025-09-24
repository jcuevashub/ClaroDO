package com.example.contactsapp.presentation.createcontact

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(10)
        
        val formatted = buildString {
            append("+1 ")
            if (digits.isNotEmpty()) {
                // Area code
                append(digits.take(3))
                if (digits.length > 3) {
                    append("-")
                    // First 3 digits of local number
                    append(digits.drop(3).take(3))
                    if (digits.length > 6) {
                        append("-")
                        // Last 4 digits
                        append(digits.drop(6).take(4))
                    }
                }
            }
        }
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val digits = text.text.take(offset).filter { it.isDigit() }.length
                return when {
                    digits == 0 -> 3 // "+1 "
                    digits <= 3 -> 3 + digits // "+1 " + area code
                    digits <= 6 -> 4 + digits // "+1 " + area + "-" + next 3
                    else -> 5 + digits // "+1 " + area + "-" + next 3 + "-" + last 4
                }
            }
            
            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 3 -> 0 // Before or at "+1 "
                    offset <= 6 -> offset - 3 // In area code
                    offset <= 10 -> offset - 4 // In first part of local number
                    else -> offset - 5 // In last part of local number
                }.coerceAtMost(text.text.length)
            }
        }
        
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}