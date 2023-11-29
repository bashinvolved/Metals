package com.danil.metals.data

import com.yandex.mapkit.geometry.Point

data class ExploredLocation (
    val id: String = "",
    val name: String = "",
    val elements: Map<String, List<Double>> = mapOf(),
    val points: List<Point> = listOf(),
    val descriptions: List<String> = listOf()
)
