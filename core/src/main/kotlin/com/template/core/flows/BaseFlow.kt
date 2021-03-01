package com.template.core.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.core.SignedSession
import com.template.core.SupportServiceHub
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

abstract class BaseFlow<out T> : FlowLogic<T>() {
    val services by lazy(LazyThreadSafetyMode.NONE) { SupportServiceHub(serviceHub) }
    protected open val interceptor: FlowInterceptor = FlowInterceptor.Empty

    fun step(step: ProgressTracker.Step) {
        progressTracker?.currentStep = step
    }

    fun signByMe(transaction: TransactionBuilder): SignedTransaction {
        return serviceHub.signInitialTransaction(transaction)
    }

    @Suspendable
    final override fun call(): T {
        interceptor.intercept(this)
        return doCall()
    }

    @Suspendable
    @Throws(FlowException::class)
    protected abstract fun doCall(): T

    @Suspendable
    fun requestSign(signer: Party, transaction: SignedTransaction): SignedSession {
        val session = initiateFlow(signer)
        val signed = subFlow(CollectSignaturesFlow(transaction, setOf(session)))
        return SignedSession(session, signed)
    }

    @Suspendable
    fun requestSign(signer: FlowSession, transaction: SignedTransaction): SignedSession {
        val signed = subFlow(CollectSignaturesFlow(transaction, setOf(signer)))
        return SignedSession(signer, signed)
    }

    @Suspendable
    fun requestNotarySign(signedSession: SignedSession): SignedTransaction {
        return subFlow(FinalityFlow(signedSession.signed, setOf(signedSession.session)))
    }
}