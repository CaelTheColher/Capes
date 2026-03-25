package me.cael.capes.handler.data

data class CosmeticaData(val cape: CapeData? = null) {
    data class CapeData(val origin: String, val image: String, val extraInfo: Int) {
        fun isAnimated() : Boolean = extraInfo > 0
    }
}