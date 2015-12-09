package Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * Created by navinislam on 12/6/15.
 */
public class ChatServer2  {
    public static String n="Navin";
    public static String s="Savin";
    public static String np="1234";
    public static String sp="5678";
    /**
     * The port that the server listens on.
     */
    private static final int PORT = 6789;

    /**
     * The set of all names of clients in the chat room.  Maintained
     * so that we can check that new clients are not registering name
     * already in use.
     */
    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<String> pass = new HashSet<String>();
    private static Hashtable<String,String> myHash= new Hashtable<String, String>();
    private static HashMap<String,String> myMap= new HashMap<String, String>();







    /**
     * The set of all the print writers for all the clients.  This
     * set is kept so we can easily broadcast messages.
     */
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    /**
     * The appplication main method, which just listens on a port and
     * spawns handler threads.
     */
    public static void main(String[] args) throws Exception {

        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
//        Socket socket= listener.accept();
//        File transferFile = new File ("/Users/navinislam/CSCI345/src/main/java/Test/testing.txt");
//        byte [] bytearray  = new byte [(int)transferFile.length()];
//        FileInputStream fin = new FileInputStream(transferFile);
//        BufferedInputStream bin = new BufferedInputStream(fin);
//        bin.read(bytearray,0,bytearray.length);
//        OutputStream os = socket.getOutputStream();
//        System.out.println("Sending Files...");
//        os.write(bytearray,0,bytearray.length);
//        os.flush();
//        socket.close();
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for a dealing with a single client
     * and broadcasting its messages.
     */
    private static class Handler extends Thread {
        public static String name;
        public static String password;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        /**
         * Constructs a handler thread, squirreling away the socket.
         * All the interesting work is done in the run method.
         */
        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Services this thread's client by repeatedly requesting a
         * screen name until a unique one has been submitted, then
         * acknowledges the name and registers the output stream for
         * the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        public void run() {
           // names.add(s);
           // names.add(n);
           // pass.add("1234");
          //  pass.add("5678");
            myMap.put("Savin","1234");
            myMap.put("Navin","5678");
            myMap.put("Brandon N","2020");
            myMap.put("Brandon S","1010");

            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Request a name from this client.  Keep requesting until
                // a name is submitted that is not already used.  Note that
                // checking for the existence of a name and adding the name
                // must be done while locking the set of names.
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    out.println("SUBMITPASS");
                    password = in.readLine();

                    //Checks to see if name and password values are null
                    if ((name == null) || (password == null)) {
                        return;
                    }


                        //Checks user name and password with hashmap
                        if (password.equalsIgnoreCase(myMap.get(name))) {
                            synchronized (names) {
                                names.add(name);
                                break;
                            }
                        }
                    }


                // Now that a successful name has been chosen, add the
                // socket's print writer to the set of all writers so
                // this client can receive broadcast messages.
                out.println("NAMEACCEPTED");
                writers.add(out);

                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}