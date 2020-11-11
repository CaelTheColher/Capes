package me.cael.capes.utils

import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

fun composeToggleText(text: MutableText, value: Boolean): Text {
    return text.append(": ").append(TranslatableText(if (value) "options.on" else "options.off"))
}