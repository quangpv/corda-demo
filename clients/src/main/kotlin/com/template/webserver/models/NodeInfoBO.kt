package com.template.webserver.models

import net.corda.core.identity.CordaX500Name
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.OK)
data class NodeInfoBO(
        val nodeName: String,
        val organisation: String
) {
    companion object {
        operator fun get(name: CordaX500Name) = NodeInfoBO(
                name.toString(),
                name.organisation
        )
    }
}