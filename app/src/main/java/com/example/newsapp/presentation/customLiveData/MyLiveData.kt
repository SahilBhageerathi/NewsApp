package com.example.newsapp.presentation.customLiveData

import android.R.attr.value
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

//        observers.forEach { (owner, callback) ->
//            // Only notify if owner is ACTIVE (STARTED or RESUMED)
//            if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
//                callback(value)
//            }
//        }
    }

    fun observe(owner: LifecycleOwner, observer:(Int)->Unit){
        val lifecycleObserver = MyLifeCycleObserver(owner)
        owner.lifecycle.addObserver(lifecycleObserver)
        observers[owner] = observer


//        // Don't register if already destroyed
//        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
//
//        // Save owner + callback in map
//        observers[owner] = callback
//
//        // Attach lifecycle watcher to auto-remove when destroyed
//        owner.lifecycle.addObserver(LifecycleBoundObserver(owner))


//        "Hey Lifecycle system, please watch this owner (Activity/Fragment) and call my " +
//        "LifecycleBoundObserver whenever its state changes (CREATED, STARTED, RESUMED, PAUSED, STOPPED, DESTROYED)


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


