package com.sleeker.velocity.data.model

data class RunWithSplits(
    val run: RunEntity,
    val splits: List<SplitData>
)

data class SplitData(
    val splitNumber: Int,
    val distance: Double,
    val duration: Long,
    val pace: Double,
    val calories: Int
)