package com.example.proyecto

import org.json.JSONObject

data class Reservation(
    val hotel: String,
    val checkIn: String,
    val checkOut: String,
    val room: String,
    val person: String,
    val bed: String
) {
    fun toJson(): String {
        val jsonObject = JSONObject()
        jsonObject.put("hotel", hotel)
        jsonObject.put("checkIn", checkIn)
        jsonObject.put("checkOut", checkOut)
        jsonObject.put("room", room)
        jsonObject.put("person", person)
        jsonObject.put("bed", bed)
        return jsonObject.toString()
    }

    companion object {
        fun fromJson(json: String): Reservation {
            val jsonObject = JSONObject(json)
            val hotel = jsonObject.getString("hotel")
            val checkIn = jsonObject.getString("checkIn")
            val checkOut = jsonObject.getString("checkOut")
            val room = jsonObject.getString("room")
            val person = jsonObject.getString("person")
            val bed = jsonObject.getString("bed")
            return Reservation(hotel, checkIn, checkOut, room, person, bed)
        }
    }
}