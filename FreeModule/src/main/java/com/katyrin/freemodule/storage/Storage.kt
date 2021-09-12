package com.katyrin.freemodule.storage

interface Storage {
    fun setCount(count: Int)
    fun getCount(): Int
}