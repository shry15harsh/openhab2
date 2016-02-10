package org.openhab.binding.nest.internal.messages;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.nest.internal.messages.DataModel.Devices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NestUpdate {

    private Logger logger = LoggerFactory.getLogger(NestUpdate.class);
    private Thing mything;

    public NestUpdate(Thing thing) {
        mything = thing;
    }

    public void updateNest(final String channelName, final Command newCommand, DataModelResponse response) {
        String property = getProperty(channelName); // thermo
        String device = getDevice(channelName); // thermo1
        String device_id = getDeviceId(device); // thermo1_device_id
        int channel_num = getChannelNum(device); // 1
        logger.info(">>> " + property + " " + device + " " + device_id + " " + channelName + " " + channel_num);

        Map.Entry<String, Thermostat>[] thermostats = response.getDevices().getThermostats().entrySet().toArray(
                (Map.Entry<String, Thermostat>[]) new Map.Entry[response.getDevices().getThermostats().size()]);
        device_id = thermostats[channel_num].getValue().getDevice_id();
        String device_name = thermostats[channel_num].getValue().getName();

        System.out.println(">>> device id: " + device_id + " device name = " + device_name);

        DataModel updateDataModel = new DataModel();
        updateDataModel.devices = new Devices();
        Thermostat thermostat = new Thermostat(null);
        updateDataModel.devices.thermostats_by_id = new HashMap<String, Thermostat>();
        updateDataModel.devices.thermostats_by_id.put(device_id, thermostat);
        updateDataModel.devices.thermostats_by_name = new HashMap<String, Thermostat>();
        updateDataModel.devices.thermostats_by_name.put(device_name, thermostat);

        BigDecimal val = new BigDecimal(35);
        thermostat.setTarget_temperature_f(val);

        System.out.println(">>>>> data model to be updated" + updateDataModel.toString());

        try {
            logger.info(">>> About to set property '{}' to '{}'", channelName, newCommand);

        } catch (Exception e) {
            logger.error("Unable to update data model", e);
        }
    }

    private String getProperty(String channelName) {
        if (channelName.contains("thermo")) {
            logger.info(">>> The Thermostat channel it is");
            return channelName.substring(8);
        } else if (channelName.contains("smoke")) {
            logger.info(">>> The smoke channel it is");
            return channelName.substring(7);
        } else if (channelName.contains("camera")) {
            logger.info(">>> The camera channel it is");
            return channelName.substring(8);
        } else {
            logger.info(">>> The general channel it is");
        }
        return channelName;
    }

    private String getDevice(String channelName) {
        if (channelName.contains("thermo")) {
            logger.info(">>> The Thermostat it is");
            return channelName.substring(0, 7);
        } else if (channelName.contains("smoke")) {
            logger.info(">>> The smoke it is");
            return channelName.substring(0, 6);
        } else if (channelName.contains("camera")) {
            logger.info(">>> The camera it is");
            return channelName.substring(0, 7);
        } else {
            logger.info("general");
            return "general";
        }
    }

    private String getDeviceId(String device) {
        return device + "_device_id";
    }

    private int getChannelNum(String device) {
        if (device.contains("thermo")) {
            return Integer.parseInt(device.substring(6, 7));
        } else if (device.contains("smoke")) {
            return Integer.parseInt(device.substring(5, 6));
        } else if (device.contains("camera")) {
            return Integer.parseInt(device.substring(6, 7));
        } else {
            return -1;
        }
    }
}
