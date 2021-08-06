package com.server_tecnologia.cokintaxi_tx;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.server_tecnologia.cokinlib.utils.JSONParser;
import com.server_tecnologia.cokintaxi_tx.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	EditText txtUsuario;
	EditText txtSenha;
	TextView link_to_register;
	Button btnLogin;
	
	// url to create new product
	private static final String url_login_taxista = "http://www.server-tecnologia.com/cokin_taxi/login_taxista.php";	

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// edit's
		txtUsuario = (EditText) findViewById(R.id.cokin_login);
		txtSenha = (EditText) findViewById(R.id.cokin_password);
		
		// save button
		btnLogin = (Button) findViewById(R.id.btnLogin);
		link_to_register = (TextView) findViewById(R.id.link_to_register);

		// button click event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				if (( txtUsuario.getText().toString().length() > 6) && ( txtSenha.getText().toString().length() > 6)) {
				   new LoginUsuario().execute();
				}  else {
					Toast.makeText(LoginActivity.this, "Usuario e/ou senhas menor que 6 caracteres !", Toast.LENGTH_LONG).show();  
				}
			}
		});

		// button click event
		link_to_register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
	            Intent intent = new Intent(getApplicationContext(), IncluirTaxistaActivity.class);
	            startActivity(intent);
	            
	        }
		});
		
		
	}

	/**
	 * Background Async Task to Create new product
	 * */
	class LoginUsuario extends AsyncTask<String, String, String> {
		
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Efetuando login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {

			String Usuario = txtUsuario.getText().toString();
			String Senha = txtSenha.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("usuario", Usuario));
			params.add(new BasicNameValuePair("senha", Senha));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_login_taxista,
					"POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// login efetuado com sucesso
					MainActivity.g_login = true;
					MainActivity.g_usuario = Usuario;
				} else {
					// failed to create product
					MainActivity.g_login = false;
					MainActivity.g_usuario = "";
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
			// dismiss the dialog once done
			
			if (MainActivity.g_login) {
				int result = 1;
				Intent returnIntent = new Intent();
				returnIntent.putExtra("result",result);
				setResult(RESULT_OK,returnIntent);
				finish();

			}else{
				AlertDialog alertDialog = new AlertDialog.Builder(
                LoginActivity.this).create();
 
				// Setting Dialog Title
				alertDialog.setTitle("Login");
 
				// Setting Dialog Message
				alertDialog.setMessage("A senha inserida está incorreta. Tente novamente !");
 
				// Setting Icon to Dialog
				//alertDialog.setIcon(R.drawable.tick);
 
				// Setting OK Button
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog closed
//						Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
					}
				});
 
				// Showing Alert Message
				alertDialog.show();
        
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent); 
			}
			pDialog.dismiss();
			//			Toast.makeText(LoginActivity.this, "A senha inserida está incorreta. Tente novamente !", Toast.LENGTH_LONG).show();
		}

	}
}