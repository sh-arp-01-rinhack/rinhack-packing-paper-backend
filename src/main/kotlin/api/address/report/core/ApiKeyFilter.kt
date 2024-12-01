package api.address.report.core

import api.address.report.service.ApiKeyService
import org.springframework.http.HttpStatus
import org.springframework.security.web.server.WebFilterChainProxy
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class ApiKeyFilter(private val apiKeyService: ApiKeyService) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val apiKey = exchange.request.headers.getFirst("X-API-KEY")

        if (!apiKeyService.validateApiKey(apiKey)) {
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            return exchange.response.setComplete()
        }
        return chain.filter(exchange)
    }
}