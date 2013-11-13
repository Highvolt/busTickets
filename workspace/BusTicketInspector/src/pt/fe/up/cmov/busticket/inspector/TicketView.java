package pt.fe.up.cmov.busticket.inspector;

import pt.fe.up.cmov.busticket.inspector.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TicketView extends LinearLayout {
	private TextView details=null;
	private TextView title=null;
	private TextView quantity=null;

	public TicketView(Context context, AttributeSet attrs) {
        super(context, attrs);
 
        LayoutInflater layoutInflater = (LayoutInflater)context
                  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=layoutInflater.inflate(R.layout.ticket_btn, this);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ticket_btn);
        details=(TextView)v.findViewById(R.id.ticketTime);
        details.setText(a.getText(R.styleable.ticket_btn_time));
        quantity=(TextView)v.findViewById(R.id.ticketQuantity);
        title=(TextView)v.findViewById(R.id.ticketType);
        title.setText(a.getText(R.styleable.ticket_btn_type));
        a.recycle();
	}
	
	public void setDetails(String a){
		details.setText(a);
	}
	public void setTitle(String a){
		title.setText(a);
	}
	public void setQuantity(String a){
		quantity.setText(a);
	}
}
