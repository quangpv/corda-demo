package com.template.webserver.provider

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
open class AppProvider {
    //    @Bean
//    open fun jsonConverter(@Autowired rpcConnection: NodeRPCConnection): MappingJackson2HttpMessageConverter {
//        val mapper = JacksonSupport.createDefaultMapper(rpcConnection.proxy)
//        val converter = MappingJackson2HttpMessageConverter()
//        converter.objectMapper = mapper
//        return converter
//    }
    @Bean
    open fun jsonConverter(): MappingJackson2HttpMessageConverter {
        return MappingJackson2HttpMessageConverter()
    }
}