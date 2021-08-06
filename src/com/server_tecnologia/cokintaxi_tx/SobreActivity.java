
package com.server_tecnologia.cokintaxi_tx;

import com.server_tecnologia.cokintaxi_tx.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;


public class SobreActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.sobre);
    
       ImageView view = (ImageView) findViewById(R.id.imageView1);
       
       RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

       //Setup anim with desired properties
       anim.setInterpolator(new LinearInterpolator());
       anim.setRepeatCount(Animation.INFINITE); //Repeat animation indefinitely
       anim.setDuration(3000); //Put desired duration per anim cycle here, in milliseconds

       //Start animation
       view.startAnimation(anim); 
   }
}
