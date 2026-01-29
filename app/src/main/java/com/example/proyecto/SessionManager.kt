package com.example.proyecto

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        const val IS_ADMIN = "is_admin"
        const val RESERVATION = "reservation"
    }

    fun saveReservation(reservation: Reservation) {
        val editor = prefs.edit()
        editor.putString(RESERVATION, reservation.toJson())
        editor.apply()
    }

    fun getReservation(): Reservation? {
        val reservationJson = prefs.getString(RESERVATION, null)
        return if (reservationJson != null) {
            Reservation.fromJson(reservationJson)
        } else {
            null
        }
    }

    fun clearReservation() {
        val editor = prefs.edit()
        editor.remove(RESERVATION)
        editor.apply()
    }

    fun setAdmin(isAdmin: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(IS_ADMIN, isAdmin)
        editor.apply()
    }

    fun isAdmin(): Boolean {
        return prefs.getBoolean(IS_ADMIN, false)
    }
}