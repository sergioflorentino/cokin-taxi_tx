package com.server_tecnologia.cokintaxi_tx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.MapActivity;
import com.server_tecnologia.cokinlib.utils.GMapV2Direction;
import com.server_tecnologia.cokintaxi.R;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class TracaRotaActivity extends FragmentActivity {
	GoogleMap mMap;
    GMapV2Direction md;

	LatLng fromPosition = new LatLng(MainActivity.gps.getLatitude(), MainActivity.gps.getLongitude());
	LatLng toPosition = new LatLng(MainActivity.PosicaoTaxista.latitude, MainActivity.PosicaoTaxista.longitude);
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        md = new GMapV2Direction();
		mMap = ((SupportMapFragment)getSupportFragmentManager()
						.findFragmentById(R.id.map)).getMap();

		LatLng coordinates = new LatLng(MainActivity.gps.getLatitude(), MainActivity.gps.getLongitude());		
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 16));
		
		mMap.addMarker(new MarkerOptions().position(fromPosition).title("Cliente"));
		mMap.addMarker(new MarkerOptions().position(toPosition).title("Taxista: "+MainActivity.nome.getText()));
		
		findDirections(fromPosition.latitude,fromPosition.longitude,toPosition.latitude,
				toPosition.longitude , GMapV2Direction.MODE_DRIVING );
		
    }
	
	public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
	{
	    Map<String, String> map = new HashMap<String, String>();
	    map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
	    map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
	    map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
	    map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
	    map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);
	 
	    GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
	    asyncTask.execute(map);
	}
	
	public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList>
	{
	    public static final String USER_CURRENT_LAT = "user_current_lat";
	    public static final String USER_CURRENT_LONG = "user_current_long";
	    public static final String DESTINATION_LAT = "destination_lat";
	    public static final String DESTINATION_LONG = "destination_long";
	    public static final String DIRECTIONS_MODE = "directions_mode";
	    private TracaRotaActivity activity;
	    private Exception exception;
	    private ProgressDialog progressDialog;
	 
	    public GetDirectionsAsyncTask(TracaRotaActivity tracaRotaActivity)
	    {
	        super();
	        this.activity = tracaRotaActivity;
	    }
	 
	    public void onPreExecute()
	    {
	        progressDialog = new ProgressDialog(activity);
	        progressDialog.setMessage("Calculating directions");
	        progressDialog.show();
	    }
	 
	    @Override
	    public void onPostExecute(ArrayList result)
	    {
	        progressDialog.dismiss();
	        if (exception == null)
	        {
	            activity.handleGetDirectionsResult(result);
	        }
	        else
	        {
	            processException();
	        }
	    }
	 
	    @Override
	    protected ArrayList doInBackground(Map<String, String>... params)
	    {
	        Map<String, String> paramMap = params[0];
	        try
	        {
	            LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)) , Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
	            LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));
	            GMapV2Direction md = new GMapV2Direction();
	            Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
	            ArrayList directionPoints = md.getDirection(doc);
	            return directionPoints;
	        }
	        catch (Exception e)
	        {
	            exception = e;
	            return null;
	        }
	    }
	 
	    private void processException()
	    {
	        //Toast.makeText(activity, activity.getString(R.string.error_when_retrieving_data), 3000).show();
	    }
	}
	
	public void handleGetDirectionsResult(ArrayList directionPoints)
	{
	    Polyline newPolyline;
	    GoogleMap mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	    PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.BLUE);
	    for(int i = 0 ; i < directionPoints.size() ; i++)
	    {
	        rectLine.add((LatLng) directionPoints.get(i));
	    }
	    newPolyline = mMap.addPolyline(rectLine);
	}
}
