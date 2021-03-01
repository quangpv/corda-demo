package com.template.core

import net.corda.core.node.ServiceHub
import net.corda.core.node.services.NetworkMapCache

class SupportServiceHub(private val serviceHub: ServiceHub) : ServiceHub by serviceHub {

    val me = MyInfoService(serviceHub)
    val network = NetworkSupportService(serviceHub.networkMapCache)
}

class MyInfoService(private val serviceHub: ServiceHub) {

    val identity get() = serviceHub.myInfo.legalIdentities.single()
}

class NetworkSupportService(private val networkMapCache: NetworkMapCache) {
    val defaultNotary get() = networkMapCache.notaryIdentities.single()
}