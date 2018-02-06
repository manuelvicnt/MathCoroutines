package com.manuelvicnt.mathcoroutines.main

sealed class MainUserAction {
    class Calculate(val number: Long) : MainUserAction()
    class FunFactEnabled(val enabled: Boolean) : MainUserAction()
}
