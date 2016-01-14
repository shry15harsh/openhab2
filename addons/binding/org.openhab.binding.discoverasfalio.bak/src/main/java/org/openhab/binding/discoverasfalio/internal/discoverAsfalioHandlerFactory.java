/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.discoverasfalio.internal;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.discoverasfalio.handler.discoverAsfalioHandler;

/**
 * The {@link discoverAsfalioHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author scaler - Initial contribution
 */
public class discoverAsfalioHandlerFactory extends BaseThingHandlerFactory {

    public static final String BINDING_ID = "discoverasfalio";
    public final static ThingTypeUID THING_TYPE_ASFALIO = new ThingTypeUID(BINDING_ID, "asfalio");

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_ASFALIO)) {// THING_TYPE_SAMPLE)) {
            return new discoverAsfalioHandler(thing);
        }

        return null;
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        // TODO Auto-generated method stub
        return false;
    }
}
