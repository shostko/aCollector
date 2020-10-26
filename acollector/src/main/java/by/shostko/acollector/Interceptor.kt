package by.shostko.acollector

fun interface Interceptor {
    fun intercept(input: EventHolder): EventHolder?
}