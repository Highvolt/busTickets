package pt.fe.up.cmov.validator;

import android.app.Application;
import android.content.Context;

public class App extends Application {
	private static Context context;

    public void onCreate(){
        super.onCreate();
        App.context = getApplicationContext();
    }
    public static void setAppContext(Context c){
    	context=c;
    }
    public static Context getAppContext() {
        return App.context;
    }
}
