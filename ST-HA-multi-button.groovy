/**
 *  send button presses to HASS
 *
 *  Copyright 2020 Iesus Sonesosn
 */
definition(
    name: "Home assistant event push",
    namespace: "tekhass",
    author: "Iesus",
    description: "send SmartThings pushbutton events to home assistant",
    category: "Convenience",
    iconUrl: "https://cdn.multitronic.fi/images/prod/E/B/WXKG02LM-1.jpg",
    iconX2Url: "https://cdn.multitronic.fi/images/prod/E/B/WXKG02LM-1.jpg",
    iconX3Url: "https://cdn.multitronic.fi/images/prod/E/B/WXKG02LM-1.jpg")


preferences {
        section("Track these Buttons:") {
        input "buttons", "capability.button", multiple: true, required: true
    }
    section ("hass Server") {
        input "hass_host", "text", title: "Home assistant Hostname/IP"
        input "hass_port", "number", title: "Home assistant Port"
        input "hass_token", "text", title: "Home assistant long lived token"
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    doSubscriptions()
}

def doSubscriptions() {
    subscribe(buttons, "button", genericHandler)
}

def genericHandler(evt) {
    def buttonNumber = parseJson(evt.data)
    def json = "{"
    json += "\"name\":\"${evt.name}\","
    json += "\"event\":\"${evt.value}\","
    json += "\"buttonNumber\":\"${buttonNumber.buttonNumber}\","
    json += "\"displayName\":\"${evt.displayName}\","
    json += "\"device\":\"${evt.device}\","
    json += "\"deviceId\":\"${evt.deviceId}\","
    json += "\"description\":\"${evt.description}\","
    json += "\"descriptionText\":\"${evt.descriptionText}\""
    json += "}"
    log.debug("JSON: ${json}")

    def params = [
    	uri: "https://${hass_host}:${hass_port}",
        path: "/api/events/tekhass.button",
        body: json,
        headers: [
            "Authorization": "Bearer ${hass_token}"
        ]
    ]

    try {
        httpPostJson(params) { resp ->
            resp.headers.each {
                log.debug "${it.name} : ${it.value}"
            }
            log.debug "response contentType: ${resp.contentType}"
        }
    } catch (e) {
    	log.debug params
        log.debug "something went wrong: $e"
    }
}
