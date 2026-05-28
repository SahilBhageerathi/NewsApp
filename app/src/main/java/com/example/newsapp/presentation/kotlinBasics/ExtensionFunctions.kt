package com.example.newsapp.presentation.kotlinBasics

import android.util.Log

fun String.add(s2: String, s3 :String ) : String {
    return this + s2+ s3
}

fun String.greet() : String {
    return "HELLO $this"
}

class Test{
    val s1 : String =" one, "
    val s2 : String=" two, "
    val s3 : String=" three."

    init {
        Log.d("Extension","they compile as static functions")
        Log.d("Extension",s1.add(s2,s3))
        Log.d("Extension",s1.greet())

        Log.d("Extension","they are evaluated at the compile time")
        val a: Animal = Dog()
        Log.d("Extension", a.sound())


        val e = Example()
        Log.d("Extension",e.greet())
    }
}

//////statically compiled
open class Animal{
    fun sound():String = "Animal"
}
open class Dog:Animal()

fun Dog.sound():String{
    return "Dog"
}

////////cannot override member functions
class Example {
    fun greet() = "I'm the member"
}

fun Example.greet() = "I'm the extension"

