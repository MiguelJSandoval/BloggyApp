package com.mjimenez.bloggy.ui.validator

import com.mjimenez.bloggy.R
import com.mjimenez.bloggy.ui.validator.base.BaseValidator
import com.mjimenez.bloggy.ui.validator.base.ValidateResult

class EmptyValidator(private val input: String) : BaseValidator() {
    override fun validate(): ValidateResult {
        val isValid = input.isNotEmpty()
        return ValidateResult(
            isValid,
            if (isValid) R.string.text_validation_success else R.string.text_validation_error_empty_field
        )
    }
}

