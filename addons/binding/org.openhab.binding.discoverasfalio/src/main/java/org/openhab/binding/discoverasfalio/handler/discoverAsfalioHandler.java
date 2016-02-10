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

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.jupnp.UpnpService;
import org.jupnp.UpnpServiceImpl;
import org.jupnp.controlpoint.ActionCallback;
import org.jupnp.controlpoint.SubscriptionCallback;
import org.jupnp.model.UnsupportedDataException;
import org.jupnp.model.action.ActionInvocation;
import org.jupnp.model.gena.CancelReason;
import org.jupnp.model.gena.GENASubscription;
import org.jupnp.model.gena.RemoteGENASubscription;
import org.jupnp.model.message.UpnpResponse;
import org.jupnp.model.message.header.STAllHeader;
import org.jupnp.model.meta.RemoteDevice;
import org.jupnp.model.meta.Service;
import org.jupnp.model.state.StateVariableValue;
import org.jupnp.model.types.InvalidValueException;
import org.jupnp.model.types.ServiceId;
import org.jupnp.model.types.UDAServiceId;
import org.jupnp.registry.DefaultRegistryListener;
import org.jupnp.registry.Registry;
import org.jupnp.registry.RegistryListener;
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

    private UpnpService upnpService;
    private RemoteDevice device_found;
    private Service switchPower;

    private SubscriptionCallback callback;

    private RegistryListener listener;

    public discoverAsfalioHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_1) || channelUID.getId().equals(CHANNEL_2)) {
            if (command.toString() == "ON") {
                logger.info(channelUID.getId() + " is ON");
                executeAction(upnpService, switchPower, channelUID.getId(), "ON");

                /*
                 * Thread clientThread = new Thread(new UpnpActionClient(channelUID.getId(), "ON"));
                 * clientThread.setDaemon(false);
                 * clientThread.start();
                 */

            } else if (command.toString() == "OFF") {
                logger.info(channelUID.getId() + " is OFF");
                executeAction(upnpService, switchPower, channelUID.getId(), "OFF");
                /*
                 * Thread clientThread = new Thread(new UpnpActionClient(channelUID.getId(), "OFF"));
                 * clientThread.setDaemon(false);
                 * clientThread.start();
                 */
            }
        }

    }

    @Override
    public void initialize() {
        Map<String, Object> properties = new HashMap<>(1);
        properties = getThing().getConfiguration().getProperties();
        System.out.println(">>>>: " + properties.get(IPADDRESS));

        upnpService = new UpnpServiceImpl();
        upnpService.startup();
        listener = createRegistryListener(upnpService);
        upnpService.getRegistry().addListener(listener);

        // Broadcast a search message for all devices
        STAllHeader sh = new STAllHeader();
        upnpService.getControlPoint().search(sh);

        updateStatus(ThingStatus.ONLINE);

    }

    private RegistryListener createRegistryListener(final UpnpService upnpService) {
        return new DefaultRegistryListener() {

            ServiceId serviceId = new UDAServiceId("SwitchPower");

            @Override
            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {

                System.out.println("device discovered: " + device.getDisplayString());

                // TODO check if the same device by ip address (or thing uid)
                // Service switchPower;
                device_found = device;
                if ((switchPower = device.findService(serviceId)) != null) {

                    System.out.println("Service discovered: " + switchPower);

                    callback = new SubscriptionCallback(switchPower, 600) {

                        @Override
                        public void established(GENASubscription sub) {
                            System.out.println("Established: " + sub.getSubscriptionId());
                        }

                        @Override
                        protected void failed(GENASubscription subscription, UpnpResponse responseStatus,
                                Exception exception, String defaultMsg) {
                            System.err.println(defaultMsg);
                        }

                        @Override
                        public void ended(GENASubscription sub, CancelReason reason, UpnpResponse response) {
                            // assertNull(reason);
                        }

                        @Override
                        public void eventReceived(GENASubscription sub) {

                            System.out.println("Event: " + sub.getCurrentSequence().getValue());

                            Map<String, StateVariableValue> values = sub.getCurrentValues();
                            // System.out.println(">>>> " + values.toString());
                            // StateVariableValue status = values.get("RecordingState");

                            // assertEquals(status.getDatatype().getClass(), BooleanDatatype.class);
                            // assertEquals(status.getDatatype().getBuiltin(), Datatype.Builtin.BOOLEAN);

                            System.out.println("Recording State is: " + values.get("RecordingState").toString());

                            System.out.println("Monitor State is: " + values.get("MonitorState").toString());

                            State value;
                            if (values.get("MonitorState").toString() == "0") {
                                value = OnOffType.OFF;
                            } else {
                                value = OnOffType.ON;
                            }
                            updateState(getThing().getChannel("monitorState").getUID(), value);

                            if (values.get("RecordingState").toString() == "0") {
                                value = OnOffType.OFF;
                            } else {
                                value = OnOffType.ON;
                            }
                            updateState(getThing().getChannel("recordingState").getUID(), value);

                        }

                        @Override
                        public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                            System.out.println("Missed events: " + numberOfMissedEvents);
                        }

                        @Override
                        protected void invalidMessage(RemoteGENASubscription sub, UnsupportedDataException ex) {
                            // Log/send an error report?
                        }

                    };

                    upnpService.getControlPoint().execute(callback);

                    // executeAction(upnpService, switchPower);
                }

            }

            @Override
            public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
                Service switchPower;
                if ((switchPower = device.findService(serviceId)) != null) {
                    System.out.println("Service disappeared: " + switchPower);
                }
            }

        };
    }

    void executeAction(final UpnpService upnpService, Service switchPowerService, String channel, String stateValue) {
        System.out.println(">>>> " + channel + " : " + stateValue);
        if (channel.equals(CHANNEL_1)) {
            monitorState = stateValue;
        } else if (channel.equals(CHANNEL_2)) {
            recordingState = stateValue;
        }
        System.out.println(">>>>>> In execute action");
        ActionInvocation setTargetInvocation = new SetTargetActionInvocation(switchPowerService);

        // Executes asynchronous in the background
        upnpService.getControlPoint().execute(new ActionCallback(setTargetInvocation) {

            @Override
            public void success(ActionInvocation invocation) {
                assert invocation.getOutput().length == 0;
                System.out.println("Successfully called action!");
                // upnpService.shutdown();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                System.err.println(defaultMsg);
                // upnpService.shutdown();
            }
        });

    }

    class SetTargetActionInvocation extends ActionInvocation {

        SetTargetActionInvocation(Service service) {
            super(service.getAction("SetTarget"));
            try {

                // Throws InvalidValueException if the value is of wrong type
                if (monitorState.equals("ON")) {
                    setInput("NewMonitorValue", true);
                    System.out.println(">>> monitorState1");

                } else if (monitorState.equals("OFF")) {
                    setInput("NewMonitorValue", false);
                    System.out.println(">>> monitorState2");
                }

                if (recordingState.equals("ON")) {
                    setInput("NewRecordingValue", true);
                    System.out.println(">>> monitorState3");

                } else if (recordingState.equals("OFF")) {
                    setInput("NewRecordingValue", false);
                    System.out.println(">>> monitorState4");
                }

            } catch (InvalidValueException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
    }

}
