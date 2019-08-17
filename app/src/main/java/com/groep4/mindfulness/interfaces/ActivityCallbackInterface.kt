package com.groep4.mindfulness.interfaces

import androidx.fragment.app.Fragment

internal interface CallbackInterface {
    fun setFragment(fragment: Fragment, addToBackstack: Boolean)
}