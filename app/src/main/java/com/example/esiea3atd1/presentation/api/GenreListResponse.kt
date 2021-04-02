package com.example.esiea3atd1.presentation.api

import com.example.esiea3atd1.presentation.list.Genres

data class GenreListResponse(
    val count: Int,
    val next: String,
    val results: List<Genres>
) {
}