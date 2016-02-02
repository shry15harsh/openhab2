/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nest;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link nestBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author harsh - Initial contribution
 */
public class nestBindingConstants {

    public static final String BINDING_ID = "nest";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "temperature");

    // List of all Channel ids
    public final static String CHANNEL_1 = "tempf";
    public final static String CHANNEL_2 = "thermo1cancool";
    public final static String CHANNEL_3 = "num_smoke";
    public final static String CHANNEL_4 = "num_camera";
    public final static String CHANNEL_5 = "thermo1_can_cool";

    public final static Integer NUM_THERMO_CHANNELS = 24;
    public final static Integer NUM_SMOKE_CHANNELS = 7;
    public final static Integer NUM_CAMERA_CHANNELS = 16;

    public static String[] CHANNELS = { "thermo1-can_cool", "thermo1-can_heat", "thermo1-is_using_emergency_heat",
            "thermo1-has_fan", "thermo1-fan_timer_active", "thermo1-fan_timer_timeout", "thermo1-has_leaf",
            "thermo1-temperature_scale", "thermo1-target_temperature_f", "thermo1-target_temperature_c",
            "thermo1-target_temperature_high_f", "thermo1-target_temperature_high_c",
            "thermo1-target_temperature_low_f", "thermo1-target_temperature_low_c", "thermo1-away_temperature_high_f",
            "thermo1-away_temperature_high_c", "thermo1-away_temperature_low_f", "thermo1-away_temperature_low_c",
            "thermo1-hvac_mode", "thermo1-ambient_temperature_f", "thermo1-ambient_temperature_c", "thermo1-humidity",
            "thermo1-hvac_state", "smoke1-battery_health", "smoke1-co_alarm_state", "smoke1-smoke_alarm_state",
            "smoke1-is_manual_test_active", "smoke1-last_manual_test_time", "smoke1-ui_color_state",
            "camera1-is_streaming", "camera1-is_audio_input_enabled", "camera1-last_is_online_change",
            "camera1-is_video_history_enabled", "camera1-web_url", "camera1-app_url", "camera1-last_event-has_sound",
            "camera1-last_event-has_motion", "camera1-last_event-start_time", "camera1-last_event-end_time",
            "camera1-last_event-urls_expire_time", "camera1-last_event-web_url", "camera1-last_event-app_url",
            "camera1-last_event-image_url", "camera1-last_event-animated_image_url" };

    public static class channel_with_type {
        private String channel_name;
        private String item_type;

        public String getChannel() {
            return this.channel_name;
        }

        public String getItemType() {
            return this.item_type;
        }

        public void setChannel(String channel_name, String item_type) {
            this.channel_name = channel_name;
            this.item_type = item_type;
        }

        public channel_with_type() {

        }
    }
}
