package edu.rutgers.rumad.rumadworkshoptwo.completed;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import edu.rutgers.rumad.rumadworkshoptwo.R;


public class MainActivity extends ActionBarActivity {

    //initialize null pointers to UI elements of interest
    Button getData;
    TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize UI elements
        getData = (Button)findViewById(R.id.getDataBtn);
        dataTextView = (TextView)findViewById(R.id.dataText);


        //we want the data to show once we press the button
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Log.d("ACTION","BUTTON PRESSED");
                    new getSugasBusData().execute();
            }
        });




    }



    private  class getSugasBusData extends AsyncTask<Void,Void,Object>{

        //base url for all our endpoints
        String baseUrl = "http://runextbus.heroku.com/";



        //this method is executes the following code in the background thread
        //so that there is no crashing because of interaction with UI Thread
        @Override
        protected Object doInBackground(Void... params) {

            //initialize http client
            HttpClient client = new DefaultHttpClient();


            //can change this to make any url you want
            String endpoint = getActiveUrl();

            //initialize a GET request with whatever url you want, im using active as an example
            HttpGet get = new HttpGet(endpoint);



            try{
                //execute the get request and store response
                HttpResponse response = client.execute(get);

                //get status code of http request
                StatusLine statusLine = response.getStatusLine();

                //initialize byte outputstream to store data
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                //store data from http response
                response.getEntity().writeTo(bos);

                //close the stream
                bos.close();

                //if the status code is OK
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){

                    //return data in a string
                    return bos.toString();

                }else{

                    //something got messed up
                    return  null;
                }

            }catch (IOException e){


                //lets see what the error was
                Log.e("ERROR",e.getMessage());

                return  null;

            }

        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            //initialize a null string to hold route titles
            String data = null;

            //ensure nothing went wrong
            if(result != null){

                try{
                    //create a json object with the data
                    JSONObject jsonObject = new JSONObject(result.toString());

                    //get an array of all the routes active, we get the 'routes' from looking at the json data
                    JSONArray routesArray = jsonObject.getJSONArray("routes");

                    if(routesArray.length() == 0){
                        dataTextView.setText("No data :(");
                        return;
                    }

                    //iterate through every route
                    for(int i = 0; i < routesArray.length(); i++){
                        //get the route at this index
                        JSONObject route = routesArray.getJSONObject(i);
                        //get title of route
                        String routeTitle = route.getString("title");
                        //store it
                        data += routeTitle + "\n";




                    }

                    //print the data!
                    dataTextView.setText(data);


                }catch(JSONException e){

                    //let's see what the error was
                    Log.e("ERROR",e.getMessage());

                    //there's no data so fill in filler text
                    dataTextView.setText("Oops, there was an error!");

                }


            }
            //uh oh result is null
            else{

                //there's no data so fill in filler text
                dataTextView.setText("Oops, there was an error");

            }


        }


        //url to get all active buses
        private  String getActiveUrl(){
            return baseUrl + "active";
        }

        //url to get predictions for each bus belonging to the route specified by tag
        private  String getRouteUrl(String tag){
            return baseUrl + "route/" + tag;
        }

        //url to get predictions for each bus belonging to the
        private  String getStopUrl(String title){
            return  baseUrl + "stop/" + title;
        }

        //url to get stops close to the given (x,y) coordinates
        private  String getNearbyUrl(double lat, double lng){
            return baseUrl + "nearby/" + lat + "/" + lng;
        }

        //url to get locations of active VEHICLES
        private String getLocationsUrl(){
            return  baseUrl + "locations";
        }

        //url to get big json data of every single route ever!!!
        private  String getConfigUrl(){
            return baseUrl + "config";
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
