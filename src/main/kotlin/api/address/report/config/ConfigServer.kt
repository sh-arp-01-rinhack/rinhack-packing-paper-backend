package api.address.report.config

import api.address.report.core.ApiKeyFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@EnableWebFluxSecurity
@Configuration
class ConfigServer(private val apiKeyFilter: ApiKeyFilter) : WebFluxConfigurer {
    @Value("\${cors.origin}")
    private val origin: String = ""

    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        corsRegistry.addMapping("/**").allowCredentials(true).allowedOrigins(origin)
            .allowedMethods("GET", "PUT", "POST", "OPTIONS", "DELETE").allowedHeaders("*").maxAge(3600)
    }

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowCredentials = true
        corsConfiguration.addAllowedHeader("*")
        corsConfiguration.addAllowedMethod("*")
        corsConfiguration.allowedOrigins = listOf("http://localhost:4200")
        corsConfiguration.addExposedHeader(HttpHeaders.SET_COOKIE)
        val corsConfigurationSource = UrlBasedCorsConfigurationSource()
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration)
        return CorsWebFilter(corsConfigurationSource)
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .cors(Customizer.withDefaults())
            .csrf{
                it.disable()
            }
            .addFilterAt(apiKeyFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange {

                it
                    .pathMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .anyExchange().permitAll()
            }
            .build()
    }
}