/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.discoverasfalio.handler;

import org.jupnp.UpnpService;
import org.jupnp.UpnpServiceImpl;
import org.jupnp.controlpoint.ActionCallback;
import org.jupnp.model.action.ActionInvocation;
import org.jupnp.model.message.UpnpResponse;
import org.jupnp.model.message.header.STAllHeader;
import org.jupnp.model.meta.RemoteDevice;
import org.jupnp.model.meta.Service;
import org.jupnp.model.types.InvalidValueException;
import org.jupnp.model.types.ServiceId;
import org.jupnp.model.types.UDAServiceId;
import org.jupnp.registry.DefaultRegistryListener;
import org.jupnp.registry.Registry;
import org.jupnp.registry.RegistryListener;

public class UpnpActionClient implements Runnable {
    private String monitorState;

    private RegistryListener listener;

    public UpnpActionClient(String monitorState) {
        this.monitorState = monitorState;
    }

    @Override
    public void run() {
        try {
            UpnpService upnpService = new UpnpServiceImpl();
            upnpService.startup();
            /*
             * ServiceId serviceId = new UDAServiceId("SwitchPower");
             * Service switchPower;
             * switchPower = asfalio_device.findService(serviceId);
             * System.out.println(">>>: switchpower thread of upnpactionclient" + switchPower);
             */

            listener = createRegistryListener(upnpService);
            upnpService.getRegistry().addListener(listener);

            // Broadcast a search message for all devices
            STAllHeader sh = new STAllHeader();
            upnpService.getControlPoint().search(sh);

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            // System.exit(1);
        }
    }

    private RegistryListener createRegistryListener(final UpnpService upnpService) {
        return new DefaultRegistryListener() {

            ServiceId serviceId = new UDAServiceId("SwitchPower");

            @Override
            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {

                System.out.println("device discovered: " + device.getDisplayString());
                Service switchPower;
                if ((switchPower = device.findService(serviceId)) != null) {

                    System.out.println("Service discovered: " + switchPower);
                    executeAction(upnpService, switchPower);
                    upnpService.getRegistry().addListener(listener);
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

    void executeAction(UpnpService upnpService, Service switchPowerService) {
        System.out.println(">>>>>> In execute action");
        ActionInvocation setTargetInvocation = new SetTargetActionInvocation(switchPowerService);

        // Executes asynchronous in the background
        upnpService.getControlPoint().execute(new ActionCallback(setTargetInvocation) {

            @Override
            public void success(ActionInvocation invocation) {
                assert invocation.getOutput().length == 0;
                System.out.println("Successfully called action!");
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                System.err.println(defaultMsg);
            }
        });

    }

    class SetTargetActionInvocation extends ActionInvocation {

        SetTargetActionInvocation(Service service) {
            super(service.getAction("SetTarget"));
            try {

                // Throws InvalidValueException if the value is of wrong type
                if (monitorState == "ON") {
                    setInput("NewTargetValue", true);

                } else if (monitorState == "OFF") {
                    setInput("NewTargetValue", false);
                }

            } catch (InvalidValueException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
    }

}
