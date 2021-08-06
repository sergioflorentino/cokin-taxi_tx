package com.server_tecnologia.cokintaxi_tx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.OverlayItem;
import com.server_tecnologia.cokinlib.utils.CustomPinPoint;
import com.server_tecnologia.cokinlib.utils.GMapV2Direction;
import com.server_tecnologia.cokinlib.utils.GPSTracker;
import com.server_tecnologia.cokinlib.utils.ImageLoader;
import com.server_tecnologia.cokinlib.utils.JSONParser;
import com.server_tecnologia.cokinlib.utils.JSONParser2;


public class MainActivity extends FragmentActivity implements OnMarkerDragListener,OnMapClickListener, OnMarkerClickListener {

	public static Handler handler_chamada;
	public static Runnable chamadaRunnable = null;
	private GoogleMap mMap;
	public static MediaPlayer mMediaPlayer;
	public static GPSTracker gps;
	public static int seek_distancia = 2;
	public static int vel_media = 25;  //
	final int RQS_GooglePlayServices = 1;
    static LatLng MinhaLocalizacao;
    static LatLng PosicaoCliente;
    GeoPoint geo;
	SupportMapFragment  mMapFragment;
	MapController mapController;
	CustomPinPoint itemizedOverlay;
	private LocationManager locationManager;
	private GeoUpdateHandler geoUpdateHandler;
	public static ArrayList<HashMap<String, String>> chamadasList;
	public static ListView lv_txdisponiveis;
	public static String g_usuario= "";
	public static int g_intervalo =  30000;   // 30 segundos
	public static Boolean g_login = false;
	public static Boolean g_forcasaida = false;
	static TextView txtcha_distancia;
	static TextView txtcha_tempo;
	static TextView subtitulo1,subtitulo2;
	TextView txtcha_qtdcarros;
	Button txtbt_aceitar;
	Button txtbt_cancelar;
	RelativeLayout panel_cliente;
    public static TextView  nome_cliente, nu_telefone;
	RatingBar aval_cliente;
	ImageView foto;

	private static final String TAG = "Cokin +Táxi";
	static String url_server = "http://www.server-tecnologia.com/cokin_taxi/";
    private static final String url_foto = url_server+"usuario/";
	static String url_chamadas = url_server+"chamadas.php";
	static String url_status_chamada = url_server+"status_chamada.php";
	static String url_fimcorrida = url_server+"fim_corrida.php";
		
	// JSON Node names
	public static final String TAG_SUCCESS = "success";
	public static final String TAG_TXCHAMADAS = "tx_chamadas";
	public static final String TAG_US_EMAIL = "ch_us_email";
	public static final String TAG_MO_EMAIL = "ch_mo_email";
	public static final String TAG_DATA = "ch_data";
	public static final String TAG_LAT = "ch_lat";
	public static final String TAG_LNG = "ch_lng";
	public static final String TAG_STATUS = "ch_status";
	public static final String TAG_DISTANCIA = "ch_distancia";
	public static final String TAG_US_NOME = "ch_us_nome";
	public static final String TAG_US_SOBRENOME = "ch_us_sobrenome";
	public static final String TAG_US_CELULAR = "ch_us_celular";
	public static final String TAG_US_FOTO = "ch_us_foto";
	public static final String TAG_US_AVAL = "ch_us_aval";
		
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	JSONParser2 jParser2 = new JSONParser2();
	private OverlayItem inDrag=null;
	Context our_context;
	JSONArray txchamadas = null;
	ImageLoader cli_foto;
	Context _context;


	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tx_main);
	    
	    try {
    		Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    		mMediaPlayer = new MediaPlayer();
    		mMediaPlayer.setDataSource(this, alert);
    		final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    		if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
    		   mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
    		   mMediaPlayer.setLooping(true);
    		   mMediaPlayer.prepare();
    		}
    	} 	catch(Exception e) {
    	}

		_context = this.getApplicationContext();
        setupLogin();

	  }
	  
	  public boolean setupLogin() {
		 if (!g_login){
		    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		    startActivityForResult(intent,1);
		    return true;
		 } else{ 
			 Taxi_Ini();
		 }
			return false;
	  }
	  
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		  if (requestCode == 1) {

		     if(resultCode == RESULT_OK){      
		         // Login Efetuado com sucesso      
		    	 Taxi_Ini();
		     }
		     if (resultCode == RESULT_CANCELED) {    
		         // Login Cancelado
		    	 //finaliza app
		    	 finish();
		     }
		  }
	  }	  
	  
	  private void Taxi_Ini(){
		    // panel motorista
			txtcha_distancia = (TextView) findViewById(R.id.cha_distancia);
			txtcha_tempo = (TextView) findViewById(R.id.cha_tempo);
			subtitulo1 = (TextView) findViewById(R.id.subtitulo1);
			subtitulo2 = (TextView) findViewById(R.id.subtitulo2);
			txtcha_qtdcarros = (TextView) findViewById(R.id.cha_qtdcarros);
			txtbt_aceitar = (Button) findViewById(R.id.bt_aceitar);
			txtbt_cancelar = (Button) findViewById(R.id.bt_cancelar);
			panel_cliente = (RelativeLayout) findViewById(R.id.panel_cliente);

			//panel cliente
			nome_cliente = (TextView) findViewById(R.id.nome_cliente);
			nu_telefone = (TextView) findViewById(R.id.nu_telefone);
			aval_cliente = (RatingBar) findViewById(R.id.aval_cliente);
			foto = (ImageView) findViewById(R.id.foto_cliente);
			cli_foto = new ImageLoader( getApplicationContext() );
			
			
		    setUpMapIfNeeded();

		    geoUpdateHandler = new GeoUpdateHandler();
		    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
		        createGpsDisabledAlert();
		    } else {
		        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
		                0,geoUpdateHandler);
		    }
		    
			// button click event
			txtbt_aceitar.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					// creating new product in background thread
					if ( chamadasList.size() > 0 ){
						
						if (txtbt_aceitar.getText().equals("Aceitar Corrida")){
							
							// Clicou em Aceitar Corrida
					    	if (mMediaPlayer.isPlaying()){
						    	mMediaPlayer.pause();
				    		}
		   	        	    if (!(handler_chamada == null)){
		   	        	    	handler_chamada.removeCallbacks(chamadaRunnable);
		   	        	    }
					 		Status_Chamada tsk_Status_Chamada =  new Status_Chamada();
					        tsk_Status_Chamada.execute("1");

						}
						if (txtbt_aceitar.getText().equals("Finalizar Corrida")){

							// Clicou em Finalizar Corrida
					    	if (mMediaPlayer.isPlaying()){
						    	mMediaPlayer.pause();
				    		}
							FimCorrida tsk_FimCorrida =  new FimCorrida();
					        tsk_FimCorrida.execute();

						}
						
						  
					}
				}
			});
			
			// button click event Esconde Cliente
			txtbt_cancelar.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
			    	if (mMediaPlayer.isPlaying()){
				    	mMediaPlayer.pause();
		    		}
			 		Status_Chamada tsk_Status_Chamada =  new Status_Chamada();
			        tsk_Status_Chamada.execute("5");

			    	Esconde_Cliente();
					mMap.clear();
					// Gravar recusa da corrida - LOG
						
				}
			});

	  }
	  
	  
	  
	  public void Exibir_Chamada(){
		  

		    if(chamadasList!=null){
		    	// atualiza com dados
		    	nome_cliente.setText(chamadasList.get(0).get(TAG_US_NOME));
		    	nu_telefone.setText(chamadasList.get(0).get(TAG_US_CELULAR));
		    	aval_cliente.setRating(Float.parseFloat(chamadasList.get(0).get(TAG_US_AVAL))); 
				if ( chamadasList.get(0).get(TAG_US_FOTO).equals("1") ){
				    cli_foto.DisplayImage(url_foto+chamadasList.get(0).get(TAG_US_EMAIL)+".jpg", foto);
				} else {	
				    foto.setImageResource(R.drawable.ic_contact_picture);
				}
				// Toca saudação
				//Fala_Saudacao();
				Exibe_Cliente();

		    }

	  }

      // Minimiza a aplicação (continua executando em background)
	  @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	      if (keyCode == KeyEvent.KEYCODE_BACK) {
	          moveTaskToBack(true);
	          return true;
	      }
	      return super.onKeyDown(keyCode, event);
	  }

	  public void Exibe_Cliente(){
		  
		  LayoutParams params_chamar = txtbt_aceitar.getLayoutParams();
		  LayoutParams params_cancelar = txtbt_cancelar.getLayoutParams();
		  params_chamar.width = LayoutParams.WRAP_CONTENT;
		  params_cancelar.width = LayoutParams.WRAP_CONTENT;
		  txtbt_aceitar.setLayoutParams(params_chamar);
		  txtbt_cancelar.setLayoutParams(params_cancelar);
		  txtbt_aceitar.setText("Aceitar Corrida");
		  txtbt_cancelar.setVisibility(View.VISIBLE);

		  panel_cliente.setVisibility(View.VISIBLE);
		  AnimationSet set = new AnimationSet(true);
		  Animation animation = new AlphaAnimation(0.0f, 1.0f);
		  animation.setDuration(100);
		  set.addAnimation(animation);
		  TranslateAnimation slide = new TranslateAnimation(0, 0, -220,0 );
		  slide.setDuration(800);
		  slide.setFillAfter(true);
		  set.addAnimation(slide);
		  
		  AnimationListener animListener = new AnimationListener() {
		      LayoutParams params = panel_cliente.getLayoutParams();
			  @Override
			  public void onAnimationStart(Animation arg0) {}            
			  @Override
			  public void onAnimationRepeat(Animation arg0) {}                   
			  @Override
			  public void onAnimationEnd(Animation arg0) {
			  	 params.height = 220;
			   	 panel_cliente.setLayoutParams(params);
			    	 //
			  }};
		  slide.setAnimationListener(animListener);
		  panel_cliente.startAnimation(slide);
	  }
	  
	  public void Esconde_Cliente(){
			LayoutParams params_aceitar = txtbt_aceitar.getLayoutParams();
		    LayoutParams params_cancelar = txtbt_cancelar.getLayoutParams();
			params_aceitar.width = LayoutParams.MATCH_PARENT;
		    params_cancelar.width = LayoutParams.MATCH_PARENT;
			txtbt_aceitar.setLayoutParams(params_aceitar);
			txtbt_cancelar.setLayoutParams(params_cancelar);
			txtbt_aceitar.setText("Aguardando chamadas...");
			txtbt_cancelar.setVisibility(View.GONE);
			
			AnimationSet set_esconde = new AnimationSet(true);
			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(100);
			set_esconde.addAnimation(animation);
			TranslateAnimation slide = new TranslateAnimation(0, 0, 0, -225); 
			slide.setDuration(800); 
			slide.setFillAfter(true);
			set_esconde.addAnimation(slide);
		
			AnimationListener anim_esconde = new AnimationListener() {
		        LayoutParams params_esconde = panel_cliente.getLayoutParams();
			     @Override
			     public void onAnimationStart(Animation arg0) {}            
			     @Override
			     public void onAnimationRepeat(Animation arg0) {

			     }                   
			     @Override
			     public void onAnimationEnd(Animation arg0) {
			    	 params_esconde.height = 220;
			    	 panel_cliente.setLayoutParams(params_esconde);
			    	 //
			     }
			 };

			slide.setAnimationListener(anim_esconde);
		    panel_cliente.startAnimation(slide);

	  }
  
	private void setUpMapIfNeeded() {
	      // Do a null check to confirm that we have not already instantiated the map.
	      if (mMap == null) {
	          // Try to obtain the map from the SupportMapFragment.
	          mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	          // Check if we were successful in obtaining the map.
	          if (mMap != null) {
	              setUpMap();        	  
	          }
	      }
	  }
	  

	  private void setUpMap() {
	      // set the zoom controls as the button panel .
  		// Carrega Posicao Atual quando abrir o app
          gps = new GPSTracker(MainActivity.this);
          // check if GPS enabled     
          if(gps.canGetLocation()){
               
              double latitude = gps.getLatitude();
              double longitude = gps.getLongitude();
               
              // \n is for new line
  			Log.d("GPS: ", "Sua localização é - Lat: " + latitude + "Long: " + longitude);                
          }else{
              // can't get location
              // GPS or Network is not enabled
              // Ask user to enable GPS/network in settings
              gps.showSettingsAlert();
          }  
          mMap.setMyLocationEnabled(true);
          mMap.setOnMapClickListener(this);
          mMap.setOnMarkerClickListener(this);          
          mMap.setOnMarkerDragListener(this);
          
	      mMap.getUiSettings().setZoomControlsEnabled(true);
	      
	      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(),gps.getLongitude() ), 17));
	      
		  // Ler a posição do taxista depois de algum tempo
	      // Loading products in Background Thread
		  handler_chamada = new Handler();
		  handler_chamada.postDelayed( chamadaRunnable = new Runnable() {
			 @Override
	 		 public void run() {
					
			     chamadasList = new ArrayList<HashMap<String, String>>();
		  		 LoadChamadas ler_chamadas =  new LoadChamadas();
		         ler_chamadas.execute();
				 handler_chamada.postDelayed(chamadaRunnable, MainActivity.g_intervalo);
			}
		  }, 200);


	      
	  }
	  
	  public boolean onKeyUp(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_MENU) {
	            // ...
	            Intent intent = new Intent(getApplicationContext(), Configuracoes.class);
	            startActivity(intent);
	            return true;
	        } else {
	            return super.onKeyUp(keyCode, event);
	        }
	    }
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    //getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	  }

	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        case R.id.menu_config:
	             Intent intent = new Intent(getApplicationContext(), Configuracoes.class);
	             startActivity(intent);
	           return true;
	        case R.id.menu_conectar:
	           //startActivity(new Intent(this, Conectar.class));
	           return true;
	        case R.id.menu_ajuda:
	            //startActivity(new Intent(this, Ajuda.class));
	            return true;
	        case R.id.menu_sobre:
	            startActivity(new Intent(this, SobreActivity.class));
	            return true;
	        case R.id.menu_legalnotices:
	 	        String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(
  	            getApplicationContext());
	 	 	    AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(MainActivity.this);
	 	 	    LicenseDialog.setTitle("Legal Notices");
	 	 	    LicenseDialog.setMessage(LicenseInfo);
	 	 	    LicenseDialog.show();
	            return true;

	        case R.id.menu_sair:
	            //startActivity(new Intent(this, Help.class));
	        	super.finish();
	            return true;

	        default:
	        }
	        return super.onOptionsItemSelected(item);
	    }
	    
	  public class GeoUpdateHandler implements LocationListener {

	      @Override
	      public void onLocationChanged(Location location) {
	          double lat = gps.getLatitude();
	          double lng = gps.getLongitude();
	          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng ), 15));
	             
	      }

	      @Override
	      public void onProviderDisabled(String provider) {
	          // TODO Auto-generated method stub

	      }

	      @Override
	      public void onProviderEnabled(String provider) {
	      }

	      @Override
	      public void onStatusChanged(String provider, int status, Bundle extras) {
	      }
	  }
	  private void createGpsDisabledAlert() {
	      AlertDialog.Builder builder = new AlertDialog.Builder(this);
	      builder
	              .setMessage(
	                      "GPS está Desligado! Gostaria de ligar?")
	              .setCancelable(false).setPositiveButton("Ligar GPS",
	                      new DialogInterface.OnClickListener() {
	                          public void onClick(DialogInterface dialog, int id) {
	                              showGpsOptions();
	                          }
	                      });
	      builder.setNegativeButton("Cancelar",
	              new DialogInterface.OnClickListener() {
	                  public void onClick(DialogInterface dialog, int id) {
	                      dialog.cancel();
	                  }
	              });
	      AlertDialog alert = builder.create();
	      alert.show();
	  }

	  private void showGpsOptions() {
	      Intent gpsOptionsIntent = new Intent(
	              android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	      startActivity(gpsOptionsIntent);
	  }
	  
	  private void TracaRota(){
		  
   	     PosicaoCliente = new LatLng(Double.parseDouble(chamadasList.get(0).get(TAG_LAT)),
			   Double.parseDouble(chamadasList.get(0).get(TAG_LNG)));
		  GMapV2Direction md;
	      md = new GMapV2Direction();
   	  	  LatLng fromPosition =  new LatLng(gps.getLatitude(), gps.getLongitude());
		  LatLng toPosition = new LatLng(PosicaoCliente.latitude, PosicaoCliente.longitude);
			
    	  findDirections(fromPosition.latitude,fromPosition.longitude,toPosition.latitude,
			toPosition.longitude , GMapV2Direction.MODE_DRIVING );
	  }
	  
	  private void AddMarkersMap(int cliente){

		  // adiciona o taxista e o cliente
		  mMap.clear();
	      MinhaLocalizacao = new LatLng(gps.getLatitude(),gps.getLongitude());
          mMap.addMarker(new MarkerOptions()
    		.position(MinhaLocalizacao)
    		.title("Taxista")
    		.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi_marker2a))
    		.snippet("Estou Aqui"));

		  // Salva posicao do cliente
		  mMap.addMarker(new MarkerOptions()
    		.position(new LatLng((Double.parseDouble(chamadasList.get(cliente).get(TAG_LAT)) ),
      				(Double.parseDouble(chamadasList.get(cliente).get(TAG_LNG)))))
	      .title("Cliente")
	      .icon(BitmapDescriptorFactory.fromResource(R.drawable.passageiro_marker))
	      .snippet(cliente+""));

	  }

	  	  
	  @Override
	  protected void onResume() {
		  // TODO Auto-generated method stub
		  super.onResume();
		  
		  int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		  
		  if (resultCode == ConnectionResult.SUCCESS){
			  //Toast.makeText(getApplicationContext(), "isGooglePlayServicesAvailable SUCCESS",
			  //Toast.LENGTH_LONG).show();
		  }else{
			  GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
		  }
		  
		  if ( g_forcasaida ){
			  g_forcasaida = false;
			  finish();
		  }
	  }
	  
		/**
		 * Background Async Task to Load all ocorrencias by making HTTP Request
		 * */
		public class LoadChamadas extends AsyncTask<String, String, String> {
			
			public void setContext(Context context){
		        our_context = context;
		    }
			
			/**
			 * Before starting background thread Show Progress Dialog
			 * */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();

			}

			/**
			 * getting All products from url
			 * */
			protected String doInBackground(String... args) {


				// Building Parameters            
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("ch_mo_email", g_usuario));
				params.add(new BasicNameValuePair("ch_mo_lat", ""+gps.getLatitude() ));
				params.add(new BasicNameValuePair("ch_mo_lng", ""+gps.getLongitude() ));
				
				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_chamadas, "POST", params);
				
				// Check your log cat for JSON response
				Log.d("Chamada:  ", json.toString());

				try {
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// products found
						// Getting Array of Products
						txchamadas = json.getJSONArray(TAG_TXCHAMADAS);
						chamadasList.clear();

						// looping through All Products
						for (int i = 0; i < txchamadas.length(); i++) {
							JSONObject c = txchamadas.getJSONObject(i);

							// Storing each json item in variable
							String us_email = c.getString(TAG_US_EMAIL);
							String mo_email = c.getString(TAG_MO_EMAIL);
							String data = c.getString(TAG_DATA);
							String lat = c.getString(TAG_LAT);
							String lng = c.getString(TAG_LNG);
							String status = c.getString(TAG_STATUS);
							String distancia = c.getString(TAG_DISTANCIA);
							String us_nome = c.getString(TAG_US_NOME);
							String us_sobrenome = c.getString(TAG_US_SOBRENOME);
							String us_celular = c.getString(TAG_US_CELULAR);
							String us_foto = c.getString(TAG_US_FOTO);
							String us_aval = c.getString(TAG_US_AVAL);

							// creating new HashMap
							HashMap<String, String> map = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							map.put(TAG_US_EMAIL, us_email);
							map.put(TAG_MO_EMAIL, mo_email);
							map.put(TAG_DATA, data);
							map.put(TAG_LAT, lat);
							map.put(TAG_LNG, lng);
							map.put(TAG_STATUS, status);
							map.put(TAG_DISTANCIA, distancia);
							map.put(TAG_US_NOME, us_nome);
							map.put(TAG_US_SOBRENOME, us_sobrenome);
							map.put(TAG_US_CELULAR, us_celular);
							map.put(TAG_US_FOTO, us_foto);
							map.put(TAG_US_AVAL, us_aval);

							// adding HashList to ArrayList
							chamadasList.add(map);
						}
					} else {

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return null;
			}

			/**
			 * After completing background task Dismiss the progress dialog
			 * **/
			protected void onPostExecute(String file_url) {

				// updating UI from Background Thread
		    	Toast.makeText(getBaseContext(), "Atualizando...", Toast.LENGTH_SHORT).show();
				
			    if (chamadasList.size() > 0) {
			    	
			    	if (!mMediaPlayer.isPlaying()){
			    	   mMediaPlayer.seekTo(0);
			    	   mMediaPlayer.start();
		    		}
            	    Exibir_Chamada();
					AddMarkersMap(0);
					TracaRota();
				
			    }

			}

		}
		
		// Atualiza status da chamada 
		public class Status_Chamada extends AsyncTask<String, String, String> {
			
			public Boolean atualizou_status = false;
			public String c_status;
			
			public void setContext(Context context){
		        our_context = context;
		    }
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected String doInBackground(String... args) {


				// Building Parameters            
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("ch_mo_email", g_usuario));
				params.add(new BasicNameValuePair("ch_us_email", chamadasList.get(0).get(TAG_US_EMAIL) ));
				params.add(new BasicNameValuePair("ch_status", args[0] ));
				
				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_status_chamada, "POST", params);
				
				// Check your log cat for JSON response
				Log.d("Alterando Status da Chamada:  ", json.toString());

				try {
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						c_status = args[0];
						atualizou_status = true;

					} else {
						atualizou_status = false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return null;
			}

			protected void onPostExecute(String file_url) {
				
			    if (atualizou_status) {

			    	if ( c_status.equals("1")){
						LayoutParams params_chamar = txtbt_aceitar.getLayoutParams();
						params_chamar.width = LayoutParams.MATCH_PARENT;
						txtbt_aceitar.setLayoutParams(params_chamar);
						txtbt_aceitar.setText("Finalizar Corrida");
						txtbt_cancelar.setVisibility(View.GONE);
			    	}else if ( c_status.equals("2")){
			    	
			    	}else if ( c_status.equals("3")){
			    	
			    	}else if ( c_status.equals("4")){
			    		
			    	}
					
			    }

			}

		}
		
		// Atualiza apaga status da corrida com 1-Aceitar		
		public class FimCorrida extends AsyncTask<String, String, String> {
			
			public Boolean fimcorrida = false;
			
			public void setContext(Context context){
		        our_context = context;
		    }
			
			/**
			 * Before starting background thread Show Progress Dialog
			 * */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			/**
			 * getting All products from url
			 * */
			protected String doInBackground(String... args) {


				// Building Parameters Posicao Final do Motorista           
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("oc_mo_email", g_usuario));
				params.add(new BasicNameValuePair("oc_us_email", chamadasList.get(0).get(TAG_US_EMAIL) ));
				params.add(new BasicNameValuePair("oc_dataini", chamadasList.get(0).get(TAG_DATA) ));
				params.add(new BasicNameValuePair("oc_latini", ""+gps.getLatitude() ));
				params.add(new BasicNameValuePair("oc_lngini", ""+gps.getLongitude() ));
				params.add(new BasicNameValuePair("oc_latfim", chamadasList.get(0).get(TAG_LAT) ));
				params.add(new BasicNameValuePair("oc_lngfim", chamadasList.get(0).get(TAG_LNG) ));
				
				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_fimcorrida, "POST", params);
				
				// Check your log cat for JSON response
				Log.d("Fim da Corrida:  ", json.toString());

				try {
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// chamada cadastrada
						fimcorrida = true;

					} else {
						fimcorrida = false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return null;
			}

			/**
			 * After completing background task Dismiss the progress dialog
			 * **/
			protected void onPostExecute(String file_url) {
				
			    if (fimcorrida) {

			    	// Fim da Corrida executado com sucesso 
					LayoutParams params_chamar = txtbt_aceitar.getLayoutParams();
					params_chamar.width = LayoutParams.MATCH_PARENT;
					txtbt_aceitar.setLayoutParams(params_chamar);
					txtbt_aceitar.setText("Aguardando chamadas...");
					txtbt_cancelar.setVisibility(View.GONE);
					Esconde_Cliente();
					mMap.clear();
					setUpMap();
				
			    }

			}

		}		
		
		public String DistanciaTempo(String chave){
			if (chave.equals("km")) {
				chave = "kilometros";
			}else if (chave.equals("m")) {
				chave = "metros";
			}else if (chave.equals("MIN")) {
				chave = "minutos";
			}else if (chave.equals("SEG")) {
				chave = "segundos";
			}
			return chave;
			
		}
		
	    public static void FormataDistancia(String distancia){
	    	String Retorno;
	    	final Double dTempo;
	    	dTempo = (Double.valueOf(distancia)*3600)/vel_media;
	    	
	    	// Seta distancia e titulo
	    	if ((Double.valueOf(distancia)*1000) > 1000){
    			Retorno = String.format("%.2f",(Double.valueOf(distancia)*1));
    			txtcha_distancia.setText(Retorno);
    	    	subtitulo1.setText("KILOMETRO"+Plural(Retorno));
	    	}else{
	    		Retorno = ""+(int) (Double.valueOf(distancia)*1000);	    			
    			txtcha_distancia.setText(Retorno);
    	    	subtitulo1.setText("METRO"+Plural(Retorno));
	    	}
	    	
	    	// Seta tempo médio e e titulo	    	
	    	if (dTempo > 59){  // mais de 60 segundos
        		Retorno = ""+(int) (Double.valueOf(dTempo)/60) ;
    	    	txtcha_tempo.setText(Retorno);
    	    	subtitulo2.setText("MINUTO"+Plural(Retorno));
	    	}else{
    			Retorno = ""+(int) (Double.valueOf(dTempo)*1);
    	    	txtcha_tempo.setText(Retorno);
    	    	subtitulo2.setText("SEGUNDO"+Plural(Retorno));
	    	}
	    	
  	    }
	    
	    public static String Plural(String cvalor){
	    	cvalor="";
	    	if (!cvalor.equals("1")){
	    		cvalor = "S";
	    	}
			return cvalor;
	    	
	    }

	    public static String TempoMedio(String distancia, Boolean tipo){
            
	    	final String Retorno;
	    	final Double dTempo;
	    	dTempo = (Double.valueOf(distancia)*3600)/vel_media;
	    	
	    	if (dTempo > 59){  // mais de 60 segundos
	    		if (tipo){
	        		Retorno = ""+(int) (Double.valueOf(dTempo)/60) ;
	    		}else{
	    			Retorno = "MINUTOS"; 
	    		}
	    	}else{
	    		if (tipo){
	    			Retorno = ""+(int) (Double.valueOf(dTempo)*1);
	    		}else{
	    			Retorno = "SEGUNDOS";	
	    		}
	    	}
	    		
			return Retorno;
	    	
	    }
	    
	    
		public String ConvertPointToLocation(GeoPoint point) {   
		   String address = "";
		   Geocoder geoCoder = new Geocoder(
		   getBaseContext(), Locale.getDefault());
		   try {
			  List<Address> addresses = geoCoder.getFromLocation(
			  point.getLatitudeE6()  / 1E6, 
			  point.getLongitudeE6() / 1E6, 1);
			 
			  if (addresses.size() > 0) {
			     for (int index = 0;
			        index < addresses.get(0).getMaxAddressLineIndex(); index++)
			        address += addresses.get(0).getAddressLine(index) + " ";
			     }
			  }
			catch (IOException e) {        
			  e.printStackTrace();
			}   
			    
			return address;
		} 
		
		@Override
		public void onMarkerDrag(Marker arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMarkerDragEnd(Marker arg0) {
			// TODO Auto-generated method stub
  			arg0.setTitle("Lat: " + arg0.getPosition().latitude + " Long: " + arg0.getPosition().longitude);
    	    GeoPoint point = new GeoPoint(
    	    	    (int) (arg0.getPosition().latitude * 1E6), 
    	    	    (int) (arg0.getPosition().longitude * 1E6));
			String address = ConvertPointToLocation(point);
			Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onMarkerDragStart(Marker arg0) {
			// TODO Auto-generated method stub
			
		}

//		@Override
//		protected boolean isRouteDisplayed() {
			// TODO Auto-generated method stub
//			return false;
//		}

		@Override
		public void onMapClick(LatLng arg0) {
			// TODO Auto-generated method stub
	    	Log.d("GPS: ", "Sua Nova localização é - Lat: " + arg0.latitude + "Long: " + arg0.longitude);
    	    GeoPoint point = new GeoPoint(
	    	    (int) (arg0.latitude * 1E6), 
	    	    (int) (arg0.longitude * 1E6));
	    	String address = ConvertPointToLocation(point);
	    	Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();
			
		}

		@Override
		public boolean onMarkerClick(Marker arg0) {
			// TODO Auto-generated method stub
            if (!arg0.getSnippet().equals("Estou Aqui")) {

            	int x_ele = Integer.parseInt(arg0.getSnippet());
            	String distancia = chamadasList.get(x_ele).get(TAG_DISTANCIA);
            	FormataDistancia(distancia);
            }else{
            	txtcha_distancia.setText("0");
            	txtcha_tempo.setText("");
            }
			
			return false;
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
		    private MainActivity activity;
		    private Exception exception;
		    private ProgressDialog progressDialog;
		 
		    public GetDirectionsAsyncTask(MainActivity tracaRota)
		    {
		        super();
		        this.activity = tracaRota;
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
		    PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.BLUE);
		    for(int i = 0 ; i < directionPoints.size() ; i++)
		    {
		        rectLine.add((LatLng) directionPoints.get(i));
		    }
		    newPolyline = mMap.addPolyline(rectLine);
		}
	    
}