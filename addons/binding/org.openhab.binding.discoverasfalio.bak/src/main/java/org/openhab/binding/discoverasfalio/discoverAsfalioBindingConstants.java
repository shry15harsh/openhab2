/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.discoverasfalio;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link discoverAsfalioBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author scaler - Initial contribution
 */
public class discoverAsfalioBindingConstants {

    public static final String BINDING_ID = "discoverasfalio";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "sample");

    // List of all Channel ids
    public final static String CHANNEL_1 = "channel1";

    public final static ThingTypeUID THING_TYPE_ASFALIO = new ThingTypeUID(BINDING_ID, "asfalio");

}
