package api.address.report.config

import api.address.report.core.ApiKeyFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class ConfigServer(private val apiKeyFilter: ApiKeyFilter) : WebFluxConfigurer {
    @Value("\${cors.origin}")
    private val origin: String = ""

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .addFilterAt(apiKeyFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange {
                it.anyExchange().permitAll()
            }
            .build()
    }
}