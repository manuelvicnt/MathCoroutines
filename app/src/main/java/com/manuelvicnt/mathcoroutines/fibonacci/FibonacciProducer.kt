package com.manuelvicnt.mathcoroutines.fibonacci

object FibonacciProducer {

    fun fib(number: Long): Long =
            if (number <= 1) {
                number
            } else {
                fib(number - 1) + fib(number - 2)
            }


//    DON'T DO THIS. IT'S NOT WORTH IT. MISUSE OF COROUTINES
//
//    private val fibonacciThreadPool = newFixedThreadPoolContext(2, "FibonacciThreadPool")
//
//    suspend fun fib(context: CoroutineContext, number: Long): Long =
//        if (number <= 1) {
//            number
//        } else {
//            async(context + fibonacciThreadPool) {
//                fib(context, number - 1)
//            }.await() +
//            async(context + fibonacciThreadPool) {
//                fib(context, number - 2)
//            }.await()
//        }
}