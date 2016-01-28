package org.openhab.binding.discoverasfalio.internal.discovery;

import static org.openhab.binding.discoverasfalio.discoverAsfalioBindingConstants.*;

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
import org.jupnp.model.types.UDAServiceId;

public class asfalioDiscoveryService implements UpnpDiscoveryParticipant {

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return Collections.singleton(THING_TYPE_ASFALIO);
    }

    @Override
    public DiscoveryResult createResult(RemoteDevice device) {
        System.out.println(">>> device info: " + device.getDisplayString());
        ThingUID uid = getThingUID(device);
        // System.out.println(uid.toString());

        if (uid != null) {
            Map<String, Object> properties = new HashMap<>(1);
            properties.put(IPADDRESS, device.getDetails().getFriendlyName());
            System.out.println(">>>>: discovery ip address" + properties.get(IPADDRESS));
            DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
                    .withLabel(device.getDetails().getFriendlyName())
                    .withRepresentationProperty(device.getDisplayString()).build();
            System.out.println(">>>>: discovery services:" + device.findServices().toString()
                    + device.findService(new UDAServiceId("SwitchPower")));
            /*
             * ServiceId serviceId = new UDAServiceId("SwitchPower");
             * Service switchPower;
             * if ((switchPower = device.findService(serviceId)) != null) {
             *
             * System.out.println("Service discovered: " + switchPower);
             * // executeAction(upnpService, switchPower);
             *
             * }
             */
            asfalio_device = device;

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
            return new ThingUID(THING_TYPE_ASFALIO, deviceUrl.replace(".", ""));
        }
        return null;
    }

}
