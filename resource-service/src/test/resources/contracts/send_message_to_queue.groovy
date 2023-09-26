package contracts

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description "Getting message from queue."

    label "song.created.event"

    input {
        triggeredBy('sendSongIdMessage()')
    }

    outputMessage {
        sentTo "resource_exchange"
        headers {
            header("contentType": "application/json")
        }
        body([
                id: 1L
        ])
    }
}
