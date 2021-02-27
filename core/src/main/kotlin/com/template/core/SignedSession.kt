package com.template.core

import net.corda.core.flows.FlowSession
import net.corda.core.transactions.SignedTransaction

class SignedSession(val session: FlowSession, val signed: SignedTransaction)