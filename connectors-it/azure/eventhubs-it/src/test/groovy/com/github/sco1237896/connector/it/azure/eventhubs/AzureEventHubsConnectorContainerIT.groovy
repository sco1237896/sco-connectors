package com.github.sco1237896.connector.it.azure.eventhubs

import com.github.sco1237896.connector.it.support.SimpleConnectorSpec
import groovy.util.logging.Slf4j
import io.restassured.http.ContentType

@Slf4j
class AzureEventHubsConnectorContainerIT extends SimpleConnectorSpec {

    def "container image exposes health and metrics"(String definition) {
        setup:
            def cnt = forDefinition(definition).build()
            cnt.start()
        when:
            def health = cnt.request.get('/q/health')
            def metrics = cnt.request.accept(ContentType.TEXT).get("/q/metrics")
        then:
            health.statusCode == 200
            metrics.statusCode == 200

            with (health.as(Map.class)) {
                status == 'UP'
                checks.find {
                    it.name == 'context' && it.status == 'UP'
                }
            }
        cleanup:
            closeQuietly(cnt)
        where:
            definition << [
                'azure_eventhubs_source_v1.yaml',
                'azure_eventhubs_sink_v1.yaml'
            ]
    }
}
