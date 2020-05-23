# ST-HA-Multi-button-remote
smartthings + home asisstant

### Why?
The built in Smartthings<-->home-assistant bridge does not send button number to home assistant, so multi button devices won't work as expected within HA.

### How
I push data to the home-assistant api to fire an event, including button number

- Install as smartApp
- publish (for me)
- configure devices, long lived token, hostname and port (no https://)
- enjoy

In home assistant, you can now create an automation that uses the button number.
```JSON
- id: test-automation-with-smartapp
  alias: test automation with button
  trigger:
    platform: event
    event_type: tekhass.button
    event_data:
      device_id: use-event-watcher-to-find-device-id
      button_number: "2"
      event: pushed
  action:
  - service: light.toggle
    data:
      entity_id:
        - light.your_light_entity
```
