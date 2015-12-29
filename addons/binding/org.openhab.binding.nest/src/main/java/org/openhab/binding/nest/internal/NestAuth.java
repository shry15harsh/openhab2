package org.openhab.binding.nest.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.eclipse.smarthome.config.core.Configuration;
//import org.openhab.binding.nest.NestBindingProvider;
import org.openhab.binding.nest.internal.messages.AccessTokenRequest;
import org.openhab.binding.nest.internal.messages.AccessTokenResponse;
import org.openhab.binding.nest.internal.messages.DataModel;
import org.openhab.binding.nest.internal.messages.DataModelRequest;
import org.openhab.binding.nest.internal.messages.DataModelResponse;
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
                    credentials.pinCode = cfg.getProperties().get("pin_code").toString();
                    if (credentials.retrieveAccessToken()) {
                        System.out.println(">>> access token retrieved is " + credentials.accessToken);
                        prefs.put("access_token", credentials.accessToken);
                    } else {
                        System.out.println(">>>naa ho pai mujhse bhai access token");
                    }
                }
            } else {
                // TODO if pin code is changed, request access token again
                // System.out.println(">>>ispe to hai bhai");
                credentials.accessToken = saved_access_token;
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
}
