package com.server_tecnologia.cokintaxi_tx;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.server_tecnologia.cokinlib.utils.ImageUploadUtility;
import com.server_tecnologia.cokinlib.utils.JSONParser;
import com.server_tecnologia.cokintaxi_tx.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class IncluirTaxistaActivity extends Activity {

	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_GALLERY = 2;
	
    // private Bitmap bitmap;
    
	// Progress Dialog
	private ProgressDialog pDialog;
    ProgressDialog simpleWaitDialog;
    public Boolean tem_foto = false;
    
	JSONParser jsonParser = new JSONParser();
	EditText txtUsuario;
	EditText txtSenha;
	EditText txtSenha2;
	EditText txtEmail;
	EditText txtTelefone,txtSobrenome,txtPlaca,txtCpf,txtEndereco,txtModelo;
	ImageView imgFoto;
	TextView link_to_login;	
	ImageButton button_camera;
	ImageButton button_file;
	public Bitmap photo; 

	Button btnCriar;
	
	// url to create new product
	private static final String url_add_taxista = "http://www.server-tecnologia.com/cokin_taxi/add_taxista.php";	
	private static final String url_foto_taxista = "http://www.server-tecnologia.com/cokin_taxi/foto_taxista.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taxista_incluir);

		// edit's
		txtUsuario = (EditText) findViewById(R.id.mo_usuario);
		txtSenha = (EditText) findViewById(R.id.mo_senha);
		txtSenha2 = (EditText) findViewById(R.id.mo_senha2);
		txtEmail = (EditText) findViewById(R.id.mo_email);
		txtTelefone = (EditText) findViewById(R.id.mo_telefone);
		txtSobrenome = (EditText) findViewById(R.id.mo_sobrenome);
		txtPlaca = (EditText) findViewById(R.id.mo_placa);
		txtCpf = (EditText) findViewById(R.id.mo_cpf);
		txtEndereco = (EditText) findViewById(R.id.mo_endereco);
		txtModelo = (EditText) findViewById(R.id.mo_modelo);
		imgFoto = (ImageView) findViewById(R.id.mo_foto);
		
		button_camera = (ImageButton) findViewById(R.id.button_camera);
		button_file = (ImageButton) findViewById(R.id.button_file);
		
		// save button
		btnCriar = (Button) findViewById(R.id.btnCriar);
		link_to_login = (TextView) findViewById(R.id.link_to_login);		

		// button click event
		btnCriar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				if ( txtSenha.getText().toString().equals(txtSenha.getText().toString()) ){
				   new CriarNovoUsuario().execute();
				}  else {
					Toast.makeText(IncluirTaxistaActivity.this, "Senhas não Conferem!", Toast.LENGTH_LONG).show();  
				}
			}
		});
		
		// button click event
		link_to_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
	            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
	            startActivity(intent);			}
		});
		
		button_camera.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
			   // call android default camera
			   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			   intent.putExtra(MediaStore.EXTRA_OUTPUT,
			   MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

			   // ******** code for crop image
			   intent.putExtra("crop", "true");
			   intent.putExtra("aspectX", 0);
			   intent.putExtra("aspectY", 0);
			   intent.putExtra("outputX", 240);
			   intent.putExtra("outputY", 240);

			   try {

			      intent.putExtra("return-data", true);
			      startActivityForResult(intent, PICK_FROM_CAMERA);

   			   } catch (ActivityNotFoundException e) {
    			// Do nothing for now
			   }
			}
		});
		
		button_file.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			   // TODO Auto-generated method stub
			   Intent intent = new Intent();
 			   // call android default gallery
			   intent.setType("image/*");
			   intent.setAction(Intent.ACTION_GET_CONTENT);
			   // ******** code for crop image
			   intent.putExtra("crop", "true");
			   intent.putExtra("aspectX", 0);
			   intent.putExtra("aspectY", 0);
			   intent.putExtra("outputX", 240);
			   intent.putExtra("outputY", 240);

			   try {

			      intent.putExtra("return-data", true);
			      startActivityForResult(Intent.createChooser(intent,"Complete action using"), PICK_FROM_GALLERY);

			   } catch (ActivityNotFoundException e) {
   			      // Do nothing for now
			   }
			}
		});

	}
	
	private class ImageTxUploaderTask extends AsyncTask<String, Integer, Void> {
		
		@Override
		protected void onPreExecute(){
			simpleWaitDialog = ProgressDialog.show(IncluirTaxistaActivity.this, "Aguarde", "Salvando Imagem");
		}
		
		@Override
		protected Void doInBackground(String... params) {
			new ImageUploadUtility().uploadSingleImage(params[0], params[1], photo, txtSenha.getText().toString());
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			simpleWaitDialog.dismiss();
		}
	}	
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	if (data !=  null){
     	   tem_foto = true;    		
    	   if (requestCode == PICK_FROM_CAMERA) {
       	      Bundle extras = data.getExtras();
    	      if (extras != null) {
    	         photo = extras.getParcelable("data");
    	         imgFoto.setImageBitmap(photo);
    	      }
    	   }

    	   if (requestCode == PICK_FROM_GALLERY) {
    	      Bundle extras2 = data.getExtras();
    	      if (extras2 != null) {
    	         photo = extras2.getParcelable("data");
    	         imgFoto.setImageBitmap(photo);
    	      }
    	   }
    	}else{
    		tem_foto = false;
    	}
    }

	/**
	 * Background Async Task to Create new product
	 * */
	class CriarNovoUsuario extends AsyncTask<String, String, String> {

		public Boolean IncluiuTaxista = false;
		public String Email_Taxista = "";
		
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(IncluirTaxistaActivity.this);
			pDialog.setMessage("Criando Conta...");
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
			String Email = txtEmail.getText().toString();
			String Telefone = txtTelefone.getText().toString();
			String Sobrenome = txtSobrenome.getText().toString();
			String Placa = txtPlaca.getText().toString();
			String Cpf = txtCpf.getText().toString();
			String Endereco = txtEndereco.getText().toString();
			String Modelo = txtModelo.getText().toString();
			Email_Taxista = Email;
			String temfoto = tem_foto ? "1" : "0";
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("mo_nome", Usuario));
			params.add(new BasicNameValuePair("mo_senha", Senha));
			params.add(new BasicNameValuePair("mo_email", Email));
			params.add(new BasicNameValuePair("mo_celular", Telefone));
			params.add(new BasicNameValuePair("mo_sobrenome", Sobrenome));
			params.add(new BasicNameValuePair("mo_placa", Placa));
			params.add(new BasicNameValuePair("mo_cpf", Cpf));
			params.add(new BasicNameValuePair("mo_endereco", Endereco));
			params.add(new BasicNameValuePair("mo_modelo", Modelo));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_add_taxista,
					"POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					IncluiuTaxista = true;
				} else {
					IncluiuTaxista = false;
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
			pDialog.dismiss();
			// se incluir com sucesso e escolheu a imagem salva a imagem no servidor
			if (IncluiuTaxista && tem_foto ){
				new ImageTxUploaderTask().execute(new String[]{ Email_Taxista , url_foto_taxista });
			}			
		}

	}
}