/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.discoverasfalio.handler;

import static org.openhab.binding.discoverasfalio.discoverAsfalioBindingConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link discoverAsfalioHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author scaler - Initial contribution
 */
public class discoverAsfalioHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(discoverAsfalioHandler.class);

    public discoverAsfalioHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_1) || channelUID.getId().equals(CHANNEL_2)) {
            if (command.toString() == "ON") {
                logger.info(channelUID.getId() + " is ON");
                Thread clientThread = new Thread(new UpnpActionClient(channelUID.getId(), "ON"));
                clientThread.setDaemon(false);
                clientThread.start();
            } else if (command.toString() == "OFF") {
                logger.info(channelUID.getId() + " is OFF");
                Thread clientThread = new Thread(new UpnpActionClient(channelUID.getId(), "OFF"));
                clientThread.setDaemon(false);
                clientThread.start();
            }
        }
    }

    @Override
    public void initialize() {
        Map<String, Object> properties = new HashMap<>(1);
        properties = getThing().getConfiguration().getProperties();
        System.out.println(">>>>: " + properties.get(IPADDRESS));

        // RemoteDevice device = asfalio_device;
        // ServiceId serviceId = new UDAServiceId("SwitchPower");
        // Service switchPower;
        updateStatus(ThingStatus.ONLINE);

    }
}
