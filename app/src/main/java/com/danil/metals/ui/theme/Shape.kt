package com.danil.metals.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shape = Shapes(
    extraSmall = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp),
    small = RoundedCornerShape(30.dp, 0.dp, 0.dp, 30.dp),
    large = RoundedCornerShape(20.dp, 0.dp, 0.dp, 0.dp),
    medium = RoundedCornerShape(0.dp, 20.dp, 20.dp, 20.dp),
    extraLarge = RoundedCornerShape(0.dp, 20.dp, 0.dp, 0.dp)
)