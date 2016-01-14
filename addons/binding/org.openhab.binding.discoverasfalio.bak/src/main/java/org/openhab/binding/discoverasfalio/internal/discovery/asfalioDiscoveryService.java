package org.openhab.binding.discoverasfalio.internal.discovery;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.UpnpDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.jupnp.model.meta.DeviceDetails;
import org.jupnp.model.meta.RemoteDevice;

public class asfalioDiscoveryService implements UpnpDiscoveryParticipant {

    private static final ThingTypeUID THING_TYPE_ASFALIO = new ThingTypeUID("discoverasfalio", "asfalio");
    private static final String ASFALIO_DEVICE_TYPE = "urn:schemas-upnp-org:device:AsfalioMonitor:1";

    private static final String IPADDRESS = null;

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return Collections.singleton(THING_TYPE_ASFALIO);
    }

    @Override
    public DiscoveryResult createResult(RemoteDevice device) {
        ThingUID uid = getThingUID(device);
        // System.out.println(uid.toString());

        if (uid != null) {
            Map<String, Object> properties = new HashMap<>(1);
            properties.put(IPADDRESS, device.getDetails().getFriendlyName());

            DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
                    .withLabel(device.getDetails().getFriendlyName())
                    .withRepresentationProperty(device.getDisplayString()).build();
            return result;
        } else {
            return null;
        }

    }

    @Override
    public ThingUID getThingUID(RemoteDevice device) {
        DeviceDetails details = device.getDetails();
        String device_type = device.getType().toString();
        if (details != null && device_type.equals(new String(ASFALIO_DEVICE_TYPE))) {
            String deviceUrl = details.getFriendlyName().toString();
            // TODO: Change UID to something standard
            return new ThingUID(THING_TYPE_ASFALIO, "5555");
        }
        return null;
    }

}
