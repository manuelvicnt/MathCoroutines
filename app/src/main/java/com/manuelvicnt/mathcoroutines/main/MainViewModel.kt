package com.manuelvicnt.mathcoroutines.main

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.manuelvicnt.mathcoroutines.fibonacci.FibonacciProducer
import com.manuelvicnt.mathcoroutines.number.NumbersApiHelper
import com.manuelvicnt.mathcoroutines.number.NumbersApiService
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.ActorScope
import kotlinx.coroutines.experimental.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.experimental.channels.actor

class MainViewModel : ViewModel() {

    private val parentJob = Job()
    private val numbersApiService = NumbersApiService(NumbersApiHelper.numbersApi)
    private var askForFunFact = false

    // Can be replaced by LiveData
    val viewStateChannel = ConflatedBroadcastChannel<MainViewState>()

    val userActionActor = actor<MainUserAction>(CommonPool, parent = parentJob) {
        for (msg in channel) { // iterate over incoming messages
            when (msg) {
                is MainUserAction.Calculate -> {
                    if (msg.number <= 0) {
                        viewStateChannel.offer(MainViewState.WrongInputError)
                    } else {
                        viewStateChannel.offer(MainViewState.Loading)
                        processCalculation(msg)
                    }
                }
                is MainUserAction.FunFactEnabled -> {
                    askForFunFact = msg.enabled
                }
            }
        }
    }

    override fun onCleared() {
        viewStateChannel.close()
        parentJob.cancel()
        super.onCleared()
    }

    private suspend fun ActorScope<MainUserAction>.processCalculation(msg: MainUserAction.Calculate) {
        val fibonacciResult: Long
        var funFactResult: String? = null

        // Make Async Requests
        val fibonacciDeferred = async(parentJob + coroutineContext) {
            FibonacciProducer.fib(msg.number)
        }
        var funFactDeferred: Deferred<String>? = null
        if (askForFunFact) {
            funFactDeferred = async(parentJob + coroutineContext) {
                numbersApiService.getNumberFunFact(coroutineContext, msg.number)
            }
        }

        // Wait for both results to come
        fibonacciResult = fibonacciDeferred.await()
        if (askForFunFact) {
            funFactDeferred?.let {
                funFactResult = it.await()
            }
        }

        // Process response
        if (askForFunFact && funFactResult != null && funFactResult == "") {
            viewStateChannel.offer(MainViewState.RequestError)
        } else {
            viewStateChannel.offer(MainViewState.Rendered(fibonacciResult, funFactResult ?: ""))
        }
    }
}