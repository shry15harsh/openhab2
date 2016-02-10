package org.openhab.binding.nest.internal;

import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.types.Command;
//import org.openhab.binding.nest.NestBindingProvider;
import org.openhab.binding.nest.internal.messages.AccessTokenRequest;
import org.openhab.binding.nest.internal.messages.AccessTokenResponse;
import org.openhab.binding.nest.internal.messages.Camera;
import org.openhab.binding.nest.internal.messages.DataModel;
import org.openhab.binding.nest.internal.messages.DataModel.Devices;
import org.openhab.binding.nest.internal.messages.DataModelRequest;
import org.openhab.binding.nest.internal.messages.DataModelResponse;
import org.openhab.binding.nest.internal.messages.SmokeCOAlarm;
import org.openhab.binding.nest.internal.messages.Structure;
import org.openhab.binding.nest.internal.messages.Thermostat;
import org.openhab.binding.nest.internal.messages.UpdateDataModelRequest;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NestAuth implements ManagedService {

    private Map<String, OAuthCredentials> credentialsCache = new HashMap<String, OAuthCredentials>();
    private static final String DEFAULT_USER_ID = "DEFAULT_USER";
    private DataModel oldDataModel = null;

    private static final Logger logger = LoggerFactory.getLogger(NestAuth.class);

    public NestAuth() {
    }

    public DataModelResponse execute(Configuration cfg) {
        try {
            /*
             * for (String userid : credentialsCache.keySet()) {
             * OAuthCredentials oauthCredentials = getOAuthCredentials(userid);
             *
             * if (oauthCredentials.noAccessToken()) {
             * if (!oauthCredentials.retrieveAccessToken()) {
             * logger.warn("Periodic poll skipped.");
             * continue;
             * }
             * }
             *
             * readNest(oauthCredentials);
             * }
             */

            String userid = DEFAULT_USER_ID;
            OAuthCredentials credentials = new OAuthCredentials(userid);
            credentials.clientId = "09cadf8f-5d83-4e46-ba3d-7aec0359cecc";
            credentials.clientSecret = "36MzuzzMjAU6eSd5tOuVqTAfL";

            Preferences prefs = credentials.getPrefsNode();
            String saved_access_token = prefs.get("access_token", null);

            if (saved_access_token == null) {
                System.out.println(">>>access_token nahi hai config me");
                if (cfg.getProperties().get("pin_code") == null) {
                    System.out.println(">>>pin code b nahi hai bhai, isko bol ki update kre config me");
                } else {
                    System.out.println(">>> chalo, pin code to hai: " + cfg.getProperties().get("pin_code"));
                    prefs.put("pin_code", cfg.getProperties().get("pin_code").toString());
                    credentials.pinCode = cfg.getProperties().get("pin_code").toString();
                    if (credentials.retrieveAccessToken()) {
                        System.out.println(">>> access token retrieved is " + credentials.accessToken);
                        prefs.put("access_token", credentials.accessToken);
                    } else {
                        System.out.println(">>>naa ho pai mujhse bhai access token");
                    }
                }
            } else {
                // TODO exceptional cases when pin is null but access token is not
                System.out.println(">>>access token hai bhai, purana to nahi h?");
                if (prefs.get("pin_code", null) == "") {
                    System.out.println(">>> pin_code is null in prefs");
                } else {
                    System.out.println(">>> pin_code " + prefs.get("pin_code", null).toString() + ", "
                            + cfg.getProperties().get("pin_code").toString());
                }
                if (prefs.get("pin_code", null).toString().equals(cfg.getProperties().get("pin_code").toString())
                        && prefs.get("pin_code", null) != "") {
                    System.out.println(">>> nahi, naya hi hai " + saved_access_token + ">>>"
                            + prefs.get("pin_code", null) + "<<<");
                    credentials.accessToken = saved_access_token;
                } else if (prefs.get("pin_code", null) == "" && prefs.get("pin_code", null).toString()
                        .equals(cfg.getProperties().get("pin_code").toString())) {
                    prefs.put("pin_code", "");
                } else {
                    System.out.println(">>> Pin code changed, requesting new access token");
                    prefs.put("pin_code", cfg.getProperties().get("pin_code").toString());
                    credentials.pinCode = cfg.getProperties().get("pin_code").toString();
                    if (credentials.retrieveAccessToken()) {
                        System.out.println(">>> new access token retrieved is " + credentials.accessToken);
                        prefs.put("access_token", credentials.accessToken);
                    } else {
                        System.out.println(">>>naa le paaya naya access token");
                    }
                }

                // System.out.println(">>>: " + credentials.accessToken);
            }

            // String configKeyTail = configKey;
            /*
             * OAuthCredentials credentials = credentialsCache.get(userid);
             * if (credentials != null) {
             * if (credentials.noAccessToken()) {
             * System.out.println(">>>: access token not found");
             * if (credentials.retrieveAccessToken()) {
             * System.out.println(">>> access token retrieved is " + credentials.accessToken);
             * credentialsCache.clear();
             * credentialsCache.put(userid, credentials);
             * } else {
             * if (credentials.pinCode != pin_code) {
             * credentials.pinCode = pin_code;
             * System.out.println(">>>: pincode updated from configuration");
             * if (credentials.retrieveAccessToken()) {
             * System.out.println(">>>access token received" + credentials.accessToken);
             * credentialsCache.clear();
             * credentialsCache.put(userid, credentials);
             * }
             * }
             * System.out.println(">>> Please get new pin code and update the thing");
             * }
             * }
             * } else {
             * System.out.println(">>>: ye to null h saala");
             * credentials = new OAuthCredentials(userid);
             * credentialsCache.put(userid, credentials);
             * /*
             * credentials is null, add new credentials
             *
             * }
             */

            /*
             * We have to figure out how to get key values from config file, aka, openhab.cfg
             */

            /*
             * Enumeration<String> configKeys = config.keys();
             * while (configKeys.hasMoreElements()) {
             * String configKey = (String) configKeys.nextElement();
             *
             * // the config-key enumeration contains additional keys that we
             * // don't want to process here ...
             * if (CONFIG_REFRESH.equals(configKey) || "service.pid".equals(configKey)) {
             * continue;
             * }
             *
             * String userid = DEFAULT_USER_ID;
             * String configKeyTail = configKey;
             *
             * OAuthCredentials credentials = credentialsCache.get(userid);
             * if (credentials == null) {
             * credentials = new OAuthCredentials(userid);
             * credentialsCache.put(userid, credentials);
             * }
             *
             * String value = (String) config.get(configKey);
             *
             * if (CONFIG_CLIENT_ID.equals(configKeyTail)) {
             * credentials.clientId = value;
             * } else if (CONFIG_CLIENT_SECRET.equals(configKeyTail)) {
             * credentials.clientSecret = value;
             * } else if (CONFIG_PIN_CODE.equals(configKeyTail)) {
             * credentials.pinCode = value;
             * } else {
             * throw new ConfigurationException(configKey, "the given configKey '" + configKey + "' is unknown");
             * }
             * }
             */

            /*
             * credentials.clientId = "09cadf8f-5d83-4e46-ba3d-7aec0359cecc";
             * credentials.clientSecret = "36MzuzzMjAU6eSd5tOuVqTAfL";
             * credentials.pinCode = "NTV2CVAJPFN39CVX";
             *
             * logger.info("id= " + credentials.clientId + " secret= " + credentials.clientSecret + " code= "
             * + credentials.pinCode);
             * /*
             * Load accesstoken from cache (already saved) and save new credentials. For now, skipping and just loading
             * by requesting.
             */
            // credentials.load();
            /*
             * if(credentials.retrieveAccessToken()==true){
             * logger.info(">>>got the access_token: "+credentials.accessToken);
             * readNest(credentials);
             * }
             *
             * credentials.accessToken =
             * "c.PuzHDofNRqedeLwBQsO2BE0akvLRj5HyXlDhWdA37bVHWsBvBirWGkpapG0jwAI7GXJITdn3CBmRr3b48gNW6hg2W8suVgW5RamMhA2TAI4TuPEgVXqepWkMqiNpSFE957crO9aXSBY8L8EB";
             * return readNest(credentials);
             *
             */
            return readNest(credentials);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.warn("Exception reading from Nest.", e);
            } else {
                logger.warn("Exception reading from Nest: {}", e.getMessage());
            }
            return null;
        }
    }

    private OAuthCredentials getOAuthCredentials(String userid) {
        if (credentialsCache.containsKey(userid)) {
            return credentialsCache.get(userid);
        } else {
            return credentialsCache.get(DEFAULT_USER_ID);
        }
    }

    private DataModelResponse readNest(OAuthCredentials oauthCredentials) throws Exception {

        DataModelRequest dmreq = new DataModelRequest(oauthCredentials.accessToken);
        DataModelResponse dmres = dmreq.execute();

        if (dmres.isError()) {
            logger.error("Error retrieving data model: {}", dmres.getError());
            return null;
        } else {
            logger.trace("Retrieved data model: {}", dmres);
        }

        DataModel newDataModel = dmres;
        this.oldDataModel = newDataModel;

        return dmres;
        // System.out.println(">>>: " + dmres.getDevices().getThermostats().toString());

        // Iterate through bindings and update all inbound values.
        /*
         * for (final NestBindingProvider provider : this.providers) {
         * for (final String itemName : provider.getItemNames()) {
         * if (provider.isInBound(itemName)) {
         * final String property = provider.getProperty(itemName);
         * final State newState = getState(newDataModel, property);
         *
         * logger.trace("Updating itemName '{}' with newState '{}'", itemName, newState);
         *
         * /*
         * we need to make sure that we won't send out this event to Nest again, when receiving it on the
         * openHAB bus
         *
         * ignoreEventList.add(new Update(itemName, newState));
         * logger.trace("Added event (item='{}', newState='{}') to the ignore event list (size={})",
         * itemName, newState, ignoreEventList.size());
         * this.eventPublisher.postUpdate(itemName, newState);
         * }
         * }
         * }
         */
    }

    private static class Update {
        private String itemName;
        private State state;

        Update(final String itemName, final State state) {
            this.itemName = itemName;
            this.state = state;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Update)) {
                return false;
            }
            return (this.itemName == null ? ((Update) o).itemName == null : this.itemName.equals(((Update) o).itemName))
                    && (this.state == null ? ((Update) o).state == null : this.state.equals(((Update) o).state));
        }

        @Override
        public int hashCode() {
            return (this.itemName == null ? 0 : this.itemName.hashCode())
                    ^ (this.state == null ? 0 : this.state.hashCode());
        }
    }

    static class OAuthCredentials {

        private static final String ACCESS_TOKEN = "accessToken";
        private static final String PIN_CODE = "pinCode";

        private String userid;

        /**
         * The private client_id needed in order to interact with the Nest API. This must be provided in the
         * <code>openhab.cfg</code> file.
         */
        private String clientId;

        /**
         * The client_secret needed when authorizing this client to the Nest API.
         *
         * @see AccessTokenRequest
         */
        private String clientSecret;

        /**
         * The pincode needed when authorizing this client to the Nest API.
         *
         * @see AccessTokenRequest
         */
        private String pinCode;

        /**
         * The access token to access the Nest API. Automatically renewed from the API using the refresh token and
         * persisted for use across activations.
         *
         * @see #refreshTokens()
         */
        String accessToken;

        public OAuthCredentials(String userid) {
            try {
                this.userid = userid;
            } catch (Exception e) {
                throw new NestException("Cannot create OAuthCredentials.", e);
            }
        }

        public Preferences getPrefsNode() {
            return Preferences.userRoot().node("org.openhab.nest." + userid);
        }

        /**
         * Only load the accessToken if the pinCode that was saved with it matches the current pinCode. Otherwise, we
         * could continue to try to use an accessToken that does not match the credentials in openhab.cfg.
         */
        private void load() {
            Preferences prefs = getPrefsNode();
            String pinCode = prefs.get(PIN_CODE, null);
            if (this.pinCode.equals(pinCode)) {
                this.accessToken = prefs.get(ACCESS_TOKEN, null);
            }
        }

        private void save() {
            Preferences prefs = getPrefsNode();
            if (this.accessToken != null) {
                prefs.put(ACCESS_TOKEN, this.accessToken);
            } else {
                prefs.remove(ACCESS_TOKEN);
            }
            if (this.pinCode != null) {
                prefs.put(PIN_CODE, this.pinCode);
            } else {
                prefs.remove(PIN_CODE);
            }
        }

        /**
         * Determine if we have an access token.
         *
         * @return <code>true</code> if we have an access token; <code>false</code> otherwise.
         */
        public boolean noAccessToken() {
            return this.accessToken == null;
        }

        /**
         * Retrieve an access token from the Nest API.
         *
         * @return <code>true</code> if we were successful, <code>false</code> otherwise
         */
        public boolean retrieveAccessToken() {
            logger.trace("Retrieving access token in order to access the Nest API.");

            final AccessTokenRequest request = new AccessTokenRequest(clientId, clientSecret, pinCode);
            logger.trace("Request: {}", request);

            final AccessTokenResponse response = request.execute();
            logger.trace("Response: {}", response);

            if (response.isError()) {
                logger.error("Error retrieving access token: {}'", response);
            }

            this.accessToken = response.getAccessToken();
            save();

            return !noAccessToken();
        }
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        // TODO Auto-generated method stub
        System.out.println(">>>:  aa gya managed service k updated me");
    }

    /* update nest */
    public void updateNest(final String channelName, final Command newCommand, DataModelResponse response) {
        String property = getProperty(channelName); // can_cool
        String device = getDevice(channelName); // thermo1
        int channel_num = getChannelNum(device) - 1; // 1

        String state_values[] = newCommand.toString().split("<=>");
        String value_device_id = state_values[1];
        String new_value = state_values[0];

        System.out.println(">>>> Value received is " + newCommand.toString() + " value= " + new_value + " device id = "
                + value_device_id);

        String device_id, device_name;

        // logger.info(">>> " + property + " " + device + " " + device_id + " " + channelName + " " + channel_num);

        DataModel updateDataModel = null;

        // Create Data Model for the corresponding update

        if (device.contains("thermo")) {
            Map.Entry<String, Thermostat>[] thermostats = response.getDevices().getThermostats().entrySet().toArray(
                    (Map.Entry<String, Thermostat>[]) new Map.Entry[response.getDevices().getThermostats().size()]);
            device_id = thermostats[channel_num].getValue().getDevice_id();
            device_name = thermostats[channel_num].getValue().getName();

            System.out.println(">>> device id: " + device_id + " device name = " + device_name);

            updateDataModel = new DataModel();
            updateDataModel.devices = new Devices();
            Thermostat thermostat = new Thermostat(null);
            updateDataModel.devices.thermostats_by_id = new HashMap<String, Thermostat>();
            updateDataModel.devices.thermostats_by_id.put(value_device_id, thermostat);
            // updateDataModel.devices.thermostats_by_name = new HashMap<String, Thermostat>();
            // updateDataModel.devices.thermostats_by_name.put(device_name, thermostat);

            if (property.equals("fan_timer_active")) {
                if (new_value.equals("true")) {
                    thermostat.setFan_timer_active(true);
                } else if (new_value.equals("false")) {
                    thermostat.setFan_timer_active(false);
                }
            } else if (property.equals("target_temperature_f")) {
                thermostat.setTarget_temperature_f(new BigDecimal(new_value));
            } else if (property.equals("target_temperature_c")) {
                thermostat.setTarget_temperature_c(new BigDecimal(new_value));
            } else if (property.equals("target_temperature_high_f")) {
                thermostat.setTarget_temperature_high_f(new BigDecimal(new_value));
            } else if (property.equals("target_temperature_high_c")) {
                thermostat.setTarget_temperature_high_c(new BigDecimal(new_value));
            } else if (property.equals("target_temperature_low_f")) {
                thermostat.setTarget_temperature_low_f(new BigDecimal(new_value));
            } else if (property.equals("target_temperature_low_c")) {
                thermostat.setTarget_temperature_low_c(new BigDecimal(new_value));
            } else if (property.equals("hvac_mode")) {
                if (new_value.equals("heat")) {
                    thermostat.setHvac_mode(Thermostat.HvacMode.HEAT);
                } else if (new_value.equals("cool")) {
                    thermostat.setHvac_mode(Thermostat.HvacMode.COOL);
                } else if (new_value.equals("heat-cool")) {
                    thermostat.setHvac_mode(Thermostat.HvacMode.HEAT_COOL);
                } else if (new_value.equals("off")) {
                    thermostat.setHvac_mode(Thermostat.HvacMode.OFF);
                }
            }

        } else if (device.contains("smoke")) {
            Map.Entry<String, SmokeCOAlarm>[] entries = new Map.Entry[response.getDevices().getSmoke_co_alarms()
                    .size()];
            Map.Entry<String, SmokeCOAlarm>[] smokes = response.getDevices().getSmoke_co_alarms().entrySet()
                    .toArray(entries);
            device_id = smokes[channel_num].getValue().getDevice_id();
            device_name = smokes[channel_num].getValue().getName();

            System.out.println(">>> device id: " + device_id + " device name = " + device_name);

            updateDataModel = new DataModel();
            updateDataModel.devices = new Devices();
            SmokeCOAlarm smokeCOAlarm = new SmokeCOAlarm(null);
            updateDataModel.devices.smoke_co_alarms_by_id = new HashMap<String, SmokeCOAlarm>();
            updateDataModel.devices.smoke_co_alarms_by_id.put(value_device_id, smokeCOAlarm);
            // updateDataModel.devices.smoke_co_alarms_by_name = new HashMap<String, SmokeCOAlarm>();
            // updateDataModel.devices.smoke_co_alarms_by_name.put(device_name, smokeCOAlarm);

        } else if (device.contains("camera")) {
            Map.Entry<String, Camera>[] entries = new Map.Entry[response.getDevices().getCameras().size()];
            Map.Entry<String, Camera>[] cameras = response.getDevices().getCameras().entrySet().toArray(entries);

            device_id = cameras[channel_num].getValue().getDevice_id();
            device_name = cameras[channel_num].getValue().getName();

            System.out.println(">>>" + device_id + " " + device_name);

            System.out.println(">>> device id: " + device_id + " device name = " + device_name);

            updateDataModel = new DataModel();
            updateDataModel.devices = new Devices();
            Camera camera = new Camera(null);
            updateDataModel.devices.cameras_by_id = new HashMap<String, Camera>();
            updateDataModel.devices.cameras_by_id.put(value_device_id, camera);
            // updateDataModel.devices.cameras_by_name = new HashMap<String, Camera>();
            // updateDataModel.devices.cameras_by_name.put(device_name, camera);

            if (property.equals("is_streaming")) {
                if (new_value.equals("true")) {
                    camera.setIs_streaming(true);
                } else if (new_value.equals("false")) {
                    camera.setIs_streaming(false);
                }
            }
        } else if (device.contains("structure")) {
            Map.Entry<String, Structure>[] structures = response.getStructures().entrySet()
                    .toArray((Map.Entry<String, Structure>[]) new Map.Entry[response.getStructures().size()]);
            String structureId = structures[0].getValue().getStructure_id();
            String structureName = structures[0].getValue().getName();

            updateDataModel = new DataModel();
            Structure structure = new Structure(null);
            updateDataModel.structures_by_id = new HashMap<String, Structure>();
            updateDataModel.structures_by_id.put(structureId, structure);
            updateDataModel.structures_by_name = new HashMap<String, Structure>();
            updateDataModel.structures_by_name.put(structureName, structure);

            if (property.equals("home_away")) {
                if (new_value.equals("auto-away")) {
                    structure.setAway(Structure.AwayState.AUTO_AWAY);
                } else if (new_value.equals("away")) {
                    structure.setAway(Structure.AwayState.AWAY);
                } else if (new_value.equals("home")) {
                    structure.setAway(Structure.AwayState.HOME);
                }
            }
        }

        System.out.println(">>>>> data model to be updated" + updateDataModel.toString());
        String userid = DEFAULT_USER_ID;
        OAuthCredentials credentials = new OAuthCredentials(userid);

        Preferences prefs = credentials.getPrefsNode();
        String access_token = prefs.get("access_token", null);

        // If we don't have an access token yet, retrieve one.
        if (access_token == null) {
            logger.warn("Sending update skipped.");
            return;
        }

        UpdateDataModelRequest request = new UpdateDataModelRequest(access_token, updateDataModel);

        try {
            DataModelResponse update_response = request.execute();
            if (update_response.isError()) {
                logger.error("Error updating data model: {}", update_response);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    // returns channel Name to be mapped with JSON
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
            return channelName;
        }
    }

    // returns prefix with device number
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
            return "structure";
        }
    }

    // returns device number
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
