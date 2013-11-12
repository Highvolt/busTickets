package pt.fe.up.cmov.busticket.client;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainBtn extends LinearLayout {
	private TextView title=null;
	private ImageView icon=null;
	String savedText;
	Drawable savedIcon;
	public Boolean enable=true;
	

	public MainBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
 
        LayoutInflater layoutInflater = (LayoutInflater)context
                  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=layoutInflater.inflate(R.layout.mainbutton, this);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.main_btn);
        title=(TextView)v.findViewById(R.id.mainTitle);
        title.setText(a.getText(R.styleable.main_btn_title));
        savedText=title.getText().toString();
        icon=(ImageView)v.findViewById(R.id.mainIcon);
        icon.setImageDrawable(a.getDrawable(R.styleable.main_btn_img));
        savedIcon=icon.getDrawable();
        a.recycle();
	}


	public void disable() {
		title.setText("Sem ligação");
		icon.setImageResource(R.drawable.close15);
		enable=false;
		
	}
	public void enable(){
		title.setText(savedText);
		icon.setImageDrawable(savedIcon);
		enable=true;
	}
	
	
}
