package com.appersiano.gdg_ledstrip_tree.views

fun Float.normalizeFrom255() : Float{
    return this * 100 / 255
}

fun Boolean.toInt() = if (this) 1 else 0