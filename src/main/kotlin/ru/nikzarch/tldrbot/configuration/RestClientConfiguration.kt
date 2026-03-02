package ru.nikzarch.tldrbot.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.net.http.HttpClient
import java.time.Duration

@Configuration
@EnableConfigurationProperties(LlmProperties::class)
class RestClientConfiguration {

    @Bean
    @Qualifier("llmRestClient")
    fun LlmRestClient(props: LlmProperties): RestClient {

        val httpClient = HttpClient.newBuilder()
            .connectTimeout(props.connectTimeout)
            .build()

        val requestFactory = JdkClientHttpRequestFactory(httpClient).apply {
            setReadTimeout(props.readTimeout)
        }

        return RestClient.builder()
            .baseUrl(props.baseUrl)
            .requestFactory(requestFactory)
            .build()
    }
}

@ConfigurationProperties(prefix = "llm")
data class LlmProperties(
    val baseUrl: String,
    val connectTimeout: Duration,
    val readTimeout: Duration
)
