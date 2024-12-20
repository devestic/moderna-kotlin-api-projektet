package com.example.modernakotlinprojektet

import kotlinx.serialization.Serializable

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.*

@Serializable
data class SuggestedDrink(val strDrink : String, val strInstructions : String)

@Serializable
data class Searchresult(val drinks : JsonElement)



