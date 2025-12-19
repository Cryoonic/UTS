package com.example.uts.viewmodel

class ProfileViewModel {

    var age: Int? = null
    var height: Float? = null
    var weight: Float? = null

    var gender: String? = null
    var activity: String? = null

    companion object {
        val instance = ProfileViewModel()
    }
}
