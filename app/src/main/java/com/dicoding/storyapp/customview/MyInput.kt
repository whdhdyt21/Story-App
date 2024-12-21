package com.dicoding.storyapp.customview

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import com.dicoding.storyapp.R
import com.google.android.material.textfield.TextInputEditText

class MyInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        when (inputType) {
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS + 1 -> {
                validateEmail(text)
            }

            InputType.TYPE_TEXT_VARIATION_PASSWORD + 1 -> {
                validatePassword(text)
            }

            InputType.TYPE_CLASS_TEXT -> {
                validateText(text)
            }
        }
    }

    private fun validateText(text: CharSequence?) {
        when {
            text.isNullOrEmpty() -> {
                setError(context.getString(R.string.signup_validation_name), null)
            }
            text.length < 3 -> {
                setError(context.getString(R.string.signup_validation_name_short), null)
            }
            else -> {
                error = null
            }
        }
    }

    private fun validateEmail(text: CharSequence?) {
        if (!text.toString().contains("@") || !text.toString().contains(".")) {
            setError(context.getString(R.string.signup_validation_email), null)
        } else {
            error = null
        }
    }

    private fun validatePassword(text: CharSequence?) {
        if (text.toString().length < 8) {
            setError(context.getString(R.string.signup_validation_password), null)
        } else {
            error = null
        }
    }
}
