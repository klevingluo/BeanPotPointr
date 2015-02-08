package com.oscode.pointr;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Code sample for accessing the Yelp API V2.
 *
 * This program demonstrates the capability of the Yelp API version 2.0 by using the Search API to
 * query for businesses by a search term and location, and the Business API to query additional
 * information about the top result from the search query.
 *
 * <p>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp Documentation</a> for more info.
 *
 */
public class YelpAPI extends AsyncTask<Void, Void, Void>{

    private static final String API_HOST = "api.yelp.com";
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";
    private static final int SEARCH_LIMIT = 19;
    private static final int SORT_TYPE = 1;
    private String term = "food";
    private String longitude = "0";
    private String latitude = "0";

    /*
     * Update OAuth credentials below from the Yelp Developers API site:
     * http://www.yelp.com/developers/getting_started/api_access
     */
    private static final String CONSUMER_KEY = "dy5UqjXG8VjyD5WUEohlkA";
    private static final String CONSUMER_SECRET = "CRy8ZgafVJl9JQ9hVLrDrOI1E4o";
    private static final String TOKEN = "w3-nGeLXLZ03oC0uqnqtJmaBj4wPEbM5";
    private static final String TOKEN_SECRET = "JWbA2yAvWzCiPYr2rxcF2mUwBKI";

    private OAuthService service;
    private Token accessToken;
    private JSONObject data;
    //private boolean isUpdated = false;

    /**
     * Setup the Yelp API OAuth credentials.
     *
     * @param consumerKey Consumer key
     * @param consumerSecret Consumer secret
     * @param token Token
     * @param tokenSecret Token secret
     */
    public YelpAPI(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey)
                        .apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    public YelpAPI(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(CONSUMER_KEY)
                        .apiSecret(CONSUMER_SECRET).build();
        this.accessToken = new Token(TOKEN, TOKEN_SECRET);
    }

    @Override
    protected Void doInBackground(Void... params) {
        this.data = queryYelp();
        return null;
    }

    /**
     * Main entry for sample Yelp API requests.
     * <p>
     * After entering your OAuth credentials, execute <tt><b>run.sh</b></tt> to run this example.
     */
    private JSONObject queryYelp() {
        YelpAPI yelpApi = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        return queryAPI(yelpApi);
    }

    /**
     * Queries the Search API based on the command line arguments and takes the first result to query
     * the Business API.
     *
     * @param yelpApi <tt>YelpAPI</tt> service instance
     */
    private static JSONObject queryAPI(YelpAPI yelpApi) {
        // Get JSON
        String searchResponseJSON =
                yelpApi.searchForBusinessesByLocation();
        JSONObject parser = null;
        try {
            parser = new JSONObject(searchResponseJSON);
        } catch (JSONException e) {
            Log.e("IMPORTANT_TAG", "json exception");
        }
        return parser;
    }

    /**
     * Creates and sends a request to the Search API by term and location.
     * <p>
     * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
     * for more info.
     *
     * @return <tt>String</tt> JSON Response
     */
    public String searchForBusinessesByLocation() {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("ll", latitude+","+longitude);
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param path API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
        return request;
    }

    /**
     * Sends an {@link OAuthRequest} and returns the {@link Response} body.
     *
     * @param request {@link OAuthRequest} corresponding to the API request
     * @return <tt>String</tt> body of API response
     */
    private String sendRequestAndGetResponse(OAuthRequest request) {
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();

        return response.getBody();
    }

    public void update(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public JSONObject getData() {
        return this.data;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}