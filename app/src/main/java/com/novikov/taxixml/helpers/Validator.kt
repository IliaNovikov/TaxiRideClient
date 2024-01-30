package com.novikov.taxixml.helpers

import android.util.Log
import com.google.android.material.textfield.TextInputLayout
import java.util.concurrent.locks.Condition
import kotlin.reflect.full.memberProperties

class Validator {

    private val validationList = ArrayList<Validation>()

    fun validate(): Boolean{

        val resultList = ArrayList<Boolean>()

        for (item in validationList){
            Log.i("class fields", item.condition::class.memberProperties.size.toString())
        }

        return false

    }

    fun addValidation(textInputLayout: TextInputLayout, condition: ValidationCondition){
        validationList.add(Validation(textInputLayout, condition))
    }

    private class Validation(val textInputLayout: TextInputLayout, val condition: ValidationCondition)

}