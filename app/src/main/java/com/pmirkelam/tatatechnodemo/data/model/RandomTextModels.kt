package com.pmirkelam.tatatechnodemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class RandomTextResponse(
    @SerializedName("randomText") val randomText: RandomText
)

@Entity(tableName = "random_texts")
data class RandomText(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @SerializedName("value") val value: String,
    @SerializedName("length") val length: Int,
    @SerializedName("created") val created: String
)