package com.example.reddittop.model.top

data class Image(
    val id: String,
    val resolutions: List<Resolution>,
    val source: Source,
)