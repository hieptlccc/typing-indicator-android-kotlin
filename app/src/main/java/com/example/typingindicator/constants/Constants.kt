package com.example.typingindicator.constants

interface Constants {
    companion object {
        const val COMETCHAT_APP_ID = "21161598565a7ea8"
        const val COMETCHAT_REGION = "us"
        const val COMETCHAT_AUTH_KEY = "0b2b492147c4fbd03cb0bd4669a3d97d0d0b7ab9"
        const val FIREBASE_REALTIME_DATABASE_URL = "https://typing-indicator-and-kotlin-default-rtdb.firebaseio.com"
        const val FIREBASE_EMAIL_KEY = "email" // this is not a secret value, it is just a constant variable that will be accessed from different places of the application.
        const val FIREBASE_USERS = "users" // this is not a secret value, it is just a constant variable that will be accessed from different places of the application.
    }
}