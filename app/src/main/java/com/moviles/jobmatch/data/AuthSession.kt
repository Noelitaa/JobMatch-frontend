package com.moviles.jobmatch.data

import com.moviles.jobmatch.data.remote.model.LoginResponse

object AuthSession {
    var currentUser: LoginResponse? = null
        private set

    fun setUser(user: LoginResponse) {
        currentUser = user
    }

    fun clear() {
        currentUser = null
    }
}
