package com.example.beerwish.data.remote.model

data class ServerResponse<T>(var meta : Meta, var response: Response<T>)

data class Meta(var code : Int)

data class Response<T>(val response: T)

data class Checkins(val count: Int, val items: List<Checkin>)

data class Checkin (
        val checkin_id: Int,
        val created_at: String,
        val checkin_comment: String,
        val user: User,
        val beer: Beer,
        val brewery: Brewery,
        val venue: Venue,
        val media: Media
)

data class User (
        val uid: Int,
        val user_name: String,
        val first_name: String,
        val last_name: String,
        val user_avatar: String,
        val location: String
) {
    fun getFullName(): String {
        return "${first_name} ${last_name}"
    }
}

data class Beer (
        val bid: Int,
        val beer_name: String,
        val beer_label: String,
        val beer_style: String
)

data class Brewery (
        val brewery_id: Int,
        val brewery_name: String
)

data class Venue (
        val venue_id: Int,
        val venue_name: String
)

data class Media (
        val count: Int,
        val items: List<Photo>
)

data class Photo (
        val photo_id: Int,
        val photo: PhotoImg
)

data class PhotoImg(
        val photo_img_sm: String,
        val photo_img_md: String,
        val photo_img_lg: String,
        val photo_img_og: String
)