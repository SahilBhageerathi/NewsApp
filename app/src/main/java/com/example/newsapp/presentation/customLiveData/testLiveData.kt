//package com.example.newsapp.presentation.customLiveData
//
//import androidx.lifecycle.LifecycleOwner
//
//open class TempLiveData(val initial:Int) {
//
//    var temp:Int = initial
//        set(value) {
//            field = value
//            notifyObservers()
//        }
//
////    val observers: MutableList<(Int)->Unit> = mutableListOf()
//    val observers: MutableMap<LifecycleOwner,(Int)->Unit> = mutableMapOf()
//
//    fun notifyObservers(){
//        observers.forEach { it.value. }
//    }
//
//    fun observe(owner: LifecycleOwner,observer:(Int)->Unit){
//        observers[owner] = observer
//    }
//
//}
