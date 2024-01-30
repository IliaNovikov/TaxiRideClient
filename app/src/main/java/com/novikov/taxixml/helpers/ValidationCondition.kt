package com.novikov.taxixml.helpers

import java.util.regex.Pattern

class ValidationCondition(val minLength: Map<Int, String>? = null,
                          val maxLength: Map<Int, String>? = null,
                          val required: Map<Boolean, String>? = null,
                          val onlyDigits: Map<Boolean, String>? = null,
                          val onlyLetters: Map<Boolean, String>? = null,
                          val pattern: Map<Pattern, String>? = null) {

    val conditionList = arrayListOf<Any?>(minLength, maxLength, required, onlyDigits, onlyLetters, pattern)


}