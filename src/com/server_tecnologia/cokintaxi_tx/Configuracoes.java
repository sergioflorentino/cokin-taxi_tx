package com.server_tecnologia.cokintaxi_tx;

import java.util.ArrayList;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.server_tecnologia.cokintaxi_tx.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class Configuracoes extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracao_lista);

        // Set up the action bar.
        if (android.os.Build.VERSION.SDK_INT >= 11){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ArrayList<ItemConfiguracao> items_conf = new ArrayList<ItemConfiguracao>();
        ItemConfiguracao item_conf1 = new ItemConfiguracao("Contas", "Gerenciar Contas cokin");
        items_conf.add(item_conf1);
//        ItemConfiguracao item_conf2 = new ItemConfiguracao("Perfil", "Ver e editar perfil");
        //items_conf.add(item_conf2);
        ItemConfiguracao item_conf3 = new ItemConfiguracao("LegalNotices", "Gerenciar Canais de Notícias");
        items_conf.add(item_conf3);
        ItemConfiguracao item_conf4 = new ItemConfiguracao("Sobre o cokin", "Verificar versão e informações legais");
        items_conf.add(item_conf4);
        ItemConfiguracao item_conf5 = new ItemConfiguracao("Sair", "Sair e efetuar logoff do cokin");
        items_conf.add(item_conf5);
        
        ListView listView = (ListView) findViewById(R.id.cf_lista);
        listView.setAdapter(new ItemConfiguracaoAdapter(this, android.R.layout.simple_list_item_1, items_conf));

        // Exibe todos os Contatos
	    listView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v,
	            int position, long id) { 
	        	
	        	   switch (position) {
                   case 0:
                   case 1:
                	     String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(
                	             getApplicationContext());
                	     AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(Configuracoes.this);
                	     LicenseDialog.setTitle("Legal Notices");
                	     LicenseDialog.setMessage(LicenseInfo);
                	     LicenseDialog.show();
                	     break;
                	   //Intent myIntent = new Intent(v.getContext(), EditarUsuarioActivity.class);
	                  //myIntent.putExtra("usuario",MainActivity.g_usuario);
	                  //startActivity(myIntent);
	                  //break;
//                   case 2:
   	        	       //  Intent myIntent2 = new Intent(v.getContext(), ListaRssActivity.class);
	                  //myIntent2.putExtra("usuario",MainActivity.g_usuario);
	                  //startActivity(myIntent2);
	                  //break;
                   case 2:
   	        	      Intent myIntent3 = new Intent(v.getContext(), SobreActivity.class);
	                  startActivity(myIntent3);
                      break;
                   case 3:
   	        	      MainActivity.g_usuario = "";
   	        	      MainActivity.g_login = false;
   	        	      MainActivity.g_forcasaida = true;
   	        	      if (!(MainActivity.handler_chamada == null)){
   	        	    	  MainActivity.handler_chamada.removeCallbacks(MainActivity.chamadaRunnable);
   	        	      }
   	        	      MainActivity.mMediaPlayer.stop();
   	        	      finish();
                      break;

	        	   }

	            }
	        });
	    
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // This is called when the Home (Up) button is pressed
	            // in the Action Bar.
	            Intent parentActivityIntent = new Intent(this, MainActivity.class);
	            parentActivityIntent.addFlags(
	                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                    Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(parentActivityIntent);
	            finish();
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}	
	public class ItemConfiguracaoAdapter extends ArrayAdapter<ItemConfiguracao> {
		private ArrayList<ItemConfiguracao> items_conf;

		public ItemConfiguracaoAdapter(Context context, int textViewResourceId, ArrayList<ItemConfiguracao> items_conf) {
			super(context, textViewResourceId, items_conf);
			this.items_conf = items_conf;
		}

		@Override 
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.configuracao_item, null);
			}
			
			ItemConfiguracao item_conf = items_conf.get(position);
			if (item_conf != null) {
				TextView ci_titulo = (TextView) v.findViewById(R.id.ci_titulo);
				TextView ci_descricao = (TextView) v.findViewById(R.id.ci_descricao);

				if (ci_titulo != null) {
					ci_titulo.setText(item_conf.ci_titulo);
				}

				if(ci_descricao != null) {
					ci_descricao.setText(item_conf.ci_descricao );
				}
			}


			return v;
		}
	}
	
	public class ItemConfiguracao {
		public String ci_titulo;
		public String ci_descricao;
		
		public ItemConfiguracao(String ci_titulo, String ci_descricao) {
			this.ci_titulo = ci_titulo;
			this.ci_descricao = ci_descricao;
		}
	}
}