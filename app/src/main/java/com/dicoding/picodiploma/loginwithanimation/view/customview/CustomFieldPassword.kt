package com.dicoding.picodiploma.loginwithanimation.view.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewParent
import com.dicoding.picodiploma.loginwithanimation.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CustomFieldPassword @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var inputLayout: TextInputLayout? = null
    private var initialized = false

    init {
        setupView()
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (initialized) validatePassword(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupView() {
        apply {
            isFocusable = true
            isFocusableInTouchMode = true
            isClickable = true
            isEnabled = true
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            gravity = Gravity.CENTER_VERTICAL
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }
    }

    private fun validatePassword(password: CharSequence?) {
        if (inputLayout == null) inputLayout = findParentLayout()
        inputLayout?.error = if (password != null && password.length < 8) {
            context.getString(R.string.validation_password)
        } else {
            null
        }
    }

    private fun findParentLayout(): TextInputLayout? {
        var parentView: ViewParent? = parent
        while (parentView != null) {
            if (parentView is TextInputLayout) return parentView
            parentView = parentView.parent
        }
        return null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initialized = true
    }
}
