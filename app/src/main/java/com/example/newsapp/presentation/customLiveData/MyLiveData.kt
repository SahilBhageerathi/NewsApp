package com.example.newsapp.presentation.customLiveData

import androidx.compose.runtime.Composable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


open class MyLiveData(val initialValue :Int) : DefaultLifecycleObserver {

    var temp:Int = initialValue
        set(value) {
            field = value
            notifyObservers()
        }

    val observers: MutableMap<LifecycleOwner,(Int)->Unit> = mutableMapOf()
    fun notifyObservers(){
        observers.forEach { entry -> entry.value.invoke(temp) }
    }

    fun observe(owner: LifecycleOwner, observer:(Int)->Unit){
        val lifecycleObserver = MyLifeCycleObserver(owner)
        owner.lifecycle.addObserver(lifecycleObserver)
        observers[owner] = observer

    }
    private fun removeObserver(owner: LifecycleOwner){
        observers.remove(owner)
    }

    inner class MyLifeCycleObserver(private val owner: LifecycleOwner) : LifecycleEventObserver{
        override fun onStateChanged(
            source: LifecycleOwner,
            event: Lifecycle.Event
        ) {
            val currentState = owner.lifecycle.currentState

            if(currentState == Lifecycle.State.DESTROYED){
                removeObserver(owner)
                owner.lifecycle.removeObserver(this)
            }
        }

    }

}

//class MyLiveData(val initialValue :Int) {
//
//    var temp:Int = initialValue
//        set(value) {
//            field = value
//            notifyObservers()
//        }
//
//    val observers: MutableList<(Int)->Unit> = mutableListOf()
//    fun notifyObservers(){
//        observers.forEach { it(temp) }
//    }
//
//    fun observe(observer:(Int)->Unit){
//        observers.add(observer)
//    }
//
//}


