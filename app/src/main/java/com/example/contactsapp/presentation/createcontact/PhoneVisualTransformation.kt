package com.example.contactsapp.presentation.createcontact

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.contactsapp.common.StringConstants

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(10)
        
        val formatted = buildString {
            append(StringConstants.PHONE_PREFIX)
            if (digits.isNotEmpty()) {
                append(digits.take(3))
                if (digits.length > 3) {
                    append(StringConstants.PHONE_SEPARATOR)
                    append(digits.drop(3).take(3))
                    if (digits.length > 6) {
                        append(StringConstants.PHONE_SEPARATOR)
                        append(digits.drop(6).take(4))
                    }
                }
            }
        }
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val digits = text.text.take(offset).filter { it.isDigit() }.length
                return when {
                    digits == 0 -> 3
                    digits <= 3 -> 3 + digits
                    digits <= 6 -> 4 + digits
                    else -> 5 + digits
                }
            }
            
            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 3 -> 0
                    offset <= 6 -> offset - 3
                    offset <= 10 -> offset - 4
                    else -> offset - 5
                }.coerceAtMost(text.text.length)
            }
        }
        
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}