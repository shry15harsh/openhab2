/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nest.handler;

import static org.openhab.binding.nest.nestBindingConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.nest.nestBindingConstants.channel_with_type;
import org.openhab.binding.nest.internal.NestAuth;
import org.openhab.binding.nest.internal.messages.DataModelResponse;
import org.openhab.binding.nest.internal.messages.Thermostat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * The {@link nestHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author harsh - Initial contribution
 */
public class nestHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(nestHandler.class);

    private static boolean channels_created = false;

    public nestHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.error(channelUID.getId() + " channel name is >>>>>>>>>>>>>>>>>>>>>> " + CHANNEL_1);
        if (channelUID.getId().equals(CHANNEL_1)) {
            System.out.println(">>>: ha ha ha,aa hi gya wapis");
            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        updateStatus(ThingStatus.ONLINE);

        Configuration cfg = editConfiguration();
        System.out.println(">>>: pin_code=" + cfg.getProperties().get("pin_code"));
        startAutomaticRefresh(cfg);

    }

    private int refresh = 10;

    private ScheduledFuture<?> refreshJob;

    public void startAutomaticRefresh(final Configuration cfg) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    NestAuth na = new NestAuth();
                    DataModelResponse response = na.execute(cfg);
                    /* Create the dynamic channels */
                    if (channels_created == false) {
                        logger.info(">>> channels are creating");
                        try {
                            createAllChannels(response);
                        } catch (Exception e) {
                            logger.error(">>>> exception aaya create channels me");
                            channels_created = true;
                        }
                    }
                    // updateChannels(response);

                } catch (Exception e) {
                    // TODO throw error when web service down (or critical error) and set thing status
                    logger.debug("Exception occurred during Refresh: {}", e);
                }
            }
        };

        refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, refresh, TimeUnit.SECONDS);
    }

    private void createAllChannels(DataModelResponse response) {
        ThingBuilder thingBuilder = editThing();
        ChannelTypeUID triggerUID = new ChannelTypeUID(BINDING_ID, "dynamic");

        /*
         * Set Channels
         */
        channel_with_type[] thermo_channels;
        thermo_channels = new channel_with_type[NUM_THERMO_CHANNELS];
        for (int i = 0; i < thermo_channels.length; ++i) {
            thermo_channels[i] = new channel_with_type();
        }
        thermo_channels[0].setChannel("can_cool", "String");
        thermo_channels[1].setChannel("can_heat", "String");
        thermo_channels[2].setChannel("is_using_emergency_heat", "String");
        thermo_channels[3].setChannel("has_fan", "String");
        thermo_channels[4].setChannel("fan_timer_active", "String");
        thermo_channels[5].setChannel("fan_timer_timeout", "String");
        thermo_channels[6].setChannel("has_leaf", "String");
        thermo_channels[7].setChannel("temperature_scale", "String");
        thermo_channels[8].setChannel("target_temperature_f", "Number");
        thermo_channels[9].setChannel("target_temperature_c", "Number");
        thermo_channels[10].setChannel("target_temperature_high_f", "Number");
        thermo_channels[11].setChannel("target_temperature_high_c", "Number");
        thermo_channels[12].setChannel("target_temperature_low_f", "Number");
        thermo_channels[13].setChannel("target_temperature_low_c", "Number");
        thermo_channels[14].setChannel("away_temperature_high_f", "Number");
        thermo_channels[15].setChannel("away_temperature_high_c", "Number");
        thermo_channels[16].setChannel("away_temperature_low_f", "Number");
        thermo_channels[17].setChannel("away_temperature_low_c", "Number");
        thermo_channels[18].setChannel("hvac_mode", "Number");
        thermo_channels[19].setChannel("ambient_temperature_f", "Number");
        thermo_channels[20].setChannel("ambient_temperature_c", "Number");
        thermo_channels[21].setChannel("humidity", "Number");
        thermo_channels[22].setChannel("hvac_state", "Number");

        channel_with_type[] smoke_channels;
        smoke_channels = new channel_with_type[NUM_SMOKE_CHANNELS];
        for (int i = 0; i < smoke_channels.length; ++i) {
            smoke_channels[i] = new channel_with_type();
        }

        smoke_channels[0].setChannel("battery_health", "String");
        smoke_channels[1].setChannel("co_alarm_state", "String");
        smoke_channels[2].setChannel("smoke_alarm_state", "String");
        smoke_channels[3].setChannel("is_manual_test_active", "String");
        smoke_channels[4].setChannel("last_manual_test_time", "String");
        smoke_channels[5].setChannel("ui_color_state", "String");

        channel_with_type[] camera_channels;
        camera_channels = new channel_with_type[NUM_CAMERA_CHANNELS];
        for (int i = 0; i < camera_channels.length; ++i) {
            camera_channels[i] = new channel_with_type();
        }

        camera_channels[0].setChannel("is_streaming", "String");
        camera_channels[1].setChannel("is_audio_input_enabled", "String");
        camera_channels[2].setChannel("last_is_online_change", "String");
        camera_channels[3].setChannel("is_video_history_enabled", "String");
        camera_channels[4].setChannel("web_url", "String");
        camera_channels[5].setChannel("app_url", "String");
        camera_channels[6].setChannel("last_eventhas_sound", "String");
        camera_channels[7].setChannel("last_eventhas_motion", "String");
        camera_channels[8].setChannel("last_eventstart_time", "String");
        camera_channels[9].setChannel("last_eventend_time", "String");
        camera_channels[10].setChannel("last_eventurls_expire_time", "String");
        camera_channels[11].setChannel("last_eventweb_url", "String");
        camera_channels[12].setChannel("last_eventapp_url", "String");
        camera_channels[13].setChannel("last_eventimage_url", "String");
        camera_channels[14].setChannel("last_eventanimated_image_url", "String");

        /*
         * Number of different devices to create channels for each of them
         */
        int num_thermostat = response.getDevices().getThermostats().size();
        int num_smoke = response.getDevices().getSmoke_co_alarms().size();
        int num_camera = response.getDevices().getCameras().size();

        Channel[] channels;
        channels = new Channel[NUM_THERMO_CHANNELS * num_thermostat + NUM_SMOKE_CHANNELS * num_smoke
                + NUM_CAMERA_CHANNELS * num_camera];
        /*
         * First loop is for number of each device and inner loop is for creating channels for them
         */
        String channel_prefix; // prefix for channel name which will differentiate different devices, either similar of
        // different
        int i; // For loop
        int channel_index = 0; // for channels array
        for (Integer j = 1; j <= num_thermostat; ++j) {
            channel_prefix = "thermo" + j.toString() + "_";
            for (i = 0; i < thermo_channels.length; ++i) {
                channels[channel_index] = ChannelBuilder
                        .create(new ChannelUID(getThing().getUID(), channel_prefix + thermo_channels[i].getChannel()),
                                thermo_channels[i].getItemType())
                        .withType(triggerUID).build();

                thingBuilder.withChannel(channels[channel_index++]);
                try {
                    addItem(channel_prefix + thermo_channels[i].getChannel(), "StringItem", "", "");
                } catch (Exception e) {
                    logger.error(">>> item " + thermo_channels[i].getChannel() + " could not be added");
                }

                try {
                    addLink(channel_prefix + thermo_channels[i].getChannel(), getThing()
                            .getChannel(channel_prefix + thermo_channels[i].getChannel()).getUID().toString());
                } catch (Exception e) {
                    logger.error(">>> link " + thermo_channels[i].getChannel() + " could not be added");
                }
            }
        }

        for (Integer j = 1; j <= num_smoke; ++j) {
            channel_prefix = "smoke" + j.toString() + "_";
            for (i = 0; i < smoke_channels.length; ++i) {
                channels[channel_index] = ChannelBuilder
                        .create(new ChannelUID(getThing().getUID(), channel_prefix + smoke_channels[i].getChannel()),
                                smoke_channels[i].getItemType())
                        .withType(triggerUID).build();

                thingBuilder.withChannel(channels[channel_index++]);
                try {
                    addItem(channel_prefix + smoke_channels[i].getChannel(), "StringItem", "", "");
                } catch (Exception e) {
                    logger.error(">>> item " + smoke_channels[i].getChannel() + " could not be added");
                }

                try {
                    addLink(channel_prefix + smoke_channels[i].getChannel(),
                            getThing().getChannel(channel_prefix + smoke_channels[i].getChannel()).getUID().toString());
                } catch (Exception e) {
                    logger.error(">>> link " + smoke_channels[i].getChannel() + " could not be added");
                }
            }
        }

        for (Integer j = 1; j <= num_camera; ++j) {
            channel_prefix = "camera" + j.toString() + "_";
            for (i = 0; i < camera_channels.length; ++i) {
                channels[channel_index] = ChannelBuilder
                        .create(new ChannelUID(getThing().getUID(), channel_prefix + camera_channels[i].getChannel()),
                                camera_channels[i].getItemType())
                        .withType(triggerUID).build();

                thingBuilder.withChannel(channels[channel_index++]);
                try {
                    addItem(channel_prefix + camera_channels[i].getChannel(), "StringItem", "", "");
                } catch (Exception e) {
                    logger.error(">>> item " + camera_channels[i].getChannel() + " could not be added");
                }
                try {
                    logger.info(">>>:" + channel_prefix + camera_channels[i].getChannel() + " , " + getThing()
                            .getChannel(channel_prefix + camera_channels[i].getChannel()).getUID().toString());
                    addLink(channel_prefix + camera_channels[i].getChannel(), getThing()
                            .getChannel(channel_prefix + camera_channels[i].getChannel()).getUID().toString());
                } catch (Exception e) {
                    logger.error(">>> link " + camera_channels[i].getChannel() + " could not be added");
                    logger.error(e.toString());
                }
            }
        }
        // thingBuilder.withChannels(channels);
        channels_created = true;
        for (i = 0; i < channels.length; ++i) {
            // logger.info(">>>uid created: " + channels[i].getUID().toString());
        }

        try {
            // updateThing(thingBuilder.build());
        } catch (Exception e) {
            logger.info(">>> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>something");
        }
        updateStatus(ThingStatus.ONLINE);

    }

    private void updateChannels(DataModelResponse response) {
        System.out.println(">>> in updatechannels()7");
        // DecimalType num_camera = new DecimalType(61);
        // State state = num_camera;
        // updateState(getThing().getChannel("testthermo_cancool").getUID(), state);
        int counter = 0;
        for (Map.Entry<String, Thermostat> thermostat : response.getDevices().getThermostats().entrySet()) {
            logger.info(">>>>> update channel loop " + counter);
            ++counter;
            State value = new StringType(thermostat.getValue().getCan_cool().toString());
            updateState(getThing().getChannel("thermo" + counter + "can_cool").getUID(), value);
        }
        // System.out.println(">>> name of thermostat " + thermostat.getValue().getName());
        // State can_cool = new StringType(thermostat.getValue().getCan_cool().toString());

        // State can_cool = new DecimalType(2);

        // System.out.println(
        // ">>> can_cool " + can_cool.toString() + " from " + thermostat.getValue().getName().toString());

        // updateState(getThing().getChannel("thermo1_can_cool").getUID(), can_cool);

        // break;
        // }
        /*
         * for (Thermostat thermostat : response.getThermostats()) {
         * System.out.println(">>>thermostat: " + thermostat.getName());
         * }
         */
    }

    private static void addItem(String itemName, String itemType, String itemLabel, String itemCategory)
            throws IOException {
        URL obj = new URL("http://localhost:8080/rest/items/" + itemName);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        JsonObject jb = new JsonObject();
        JsonArray jb2 = new JsonArray();
        jb.addProperty("type", itemType);
        jb.addProperty("label", itemLabel);
        jb.addProperty("category", itemCategory);
        jb.add("tags", jb2);
        jb.add("groupNames", jb2);
        // System.out.print(jb.toString());

        // For POST only - START
        con.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(jb.toString());
        wr.flush();
        wr.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        // System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            // System.out.println(response.toString());
        } else {
            System.out.println("POST request not worked");
        }
    }

    private static void addLink(String itemName, String channelName) throws IOException {
        URL obj = new URL("http://localhost:8080/rest/links/" + itemName + "/" + channelName);
        System.out.println(">>> in addlink: itemName=" + itemName + " channelName=" + channelName);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        // For POST only - START
        con.setDoOutput(true);
        // For POST only - END

        int responseCode = con.getResponseCode();
        // System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            // System.out.println(response.toString());
        } else {
            System.out.println("PUT link request not worked");
        }
    }

    private static void removeItem(String itemName) throws IOException {
        URL obj = new URL("http://localhost:8080/rest/items/" + itemName);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // For POST only - START
        con.setDoOutput(true);
        // OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        // wr.write(jb.toString());
        // wr.flush();
        // wr.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println(">>> delete request not worked");
        }
    }

    @Override
    public void dispose() {
        refreshJob.cancel(true);
        logger.debug("Setting status for thing '{}' to OFFLINE", getThing().getUID());
        updateStatus(ThingStatus.OFFLINE);
    }

}
