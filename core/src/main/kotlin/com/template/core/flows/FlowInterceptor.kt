package com.template.core.flows

import net.corda.core.flows.FlowLogic

interface FlowInterceptor {
    fun intercept(flow: FlowLogic<*>)

    object Empty : FlowInterceptor {
        override fun intercept(flow: FlowLogic<*>) {}
    }

    class All(vararg interceptors: FlowInterceptor) : FlowInterceptor {
        private val mInterceptors = interceptors
        override fun intercept(flow: FlowLogic<*>) {
            for (mInterceptor in mInterceptors) {
                mInterceptor.intercept(flow)
            }
        }
    }
}