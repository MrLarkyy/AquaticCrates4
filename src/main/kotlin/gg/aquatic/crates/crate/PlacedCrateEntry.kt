package gg.aquatic.crates.crate

data class PlacedCrateEntry(
    val worldName: String,
    val crateId: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
) {
    fun serialized(): String = "$crateId;$x;$y;$z;$yaw"
}
