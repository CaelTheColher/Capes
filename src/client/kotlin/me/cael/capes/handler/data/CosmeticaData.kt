package me.cael.capes.handler.data

data class CosmeticaData(val cloak: CapeData? = null) {
    data class CapeData(val texture: String, val frames: Int) {
        fun isAnimated() : Boolean = frames > 1
    }
}