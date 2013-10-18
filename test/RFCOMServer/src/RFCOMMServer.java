import java.io.*;
import javax.microedition.io.*;
import javax.bluetooth.*;

public class RFCOMMServer {

    public static void main( String args[] ) {
        try {
            String a="0000120100001000800000805f9b34fb";
            String url = "btspp://localhost:" +
                    a +
                    ";name=SampleServer";
            System.out.println(a);
            StreamConnectionNotifier service =
                    (StreamConnectionNotifier) Connector.open( url );
            System.out.println("service up!");
            StreamConnection con =
                    (StreamConnection) service.acceptAndOpen();
            System.out.println("service connected!");
            OutputStream os = con.openOutputStream();
            InputStream is = con.openInputStream();

            String greeting = "JSR-82 RFCOMM server says hello";
            os.write( greeting.getBytes() );

            byte buffer[] = new byte[80];
            int bytes_read = is.read( buffer );
            String received = new String(buffer, 0, bytes_read);
            System.out.println("received: " + received);

            con.close();
        } catch ( IOException e ) {
            System.err.print(e.toString());
        }
    }
}
