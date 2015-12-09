package Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;


/**
 * Created by navinislam on 12/6/15.
 */
public class ChatClient2 {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("J-IM");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);

    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Return in the
     * listener sends the textfield contents to the server.  Note
     * however that the textfield is initially NOT editable, and
     * only becomes editable AFTER the client receives the NAMEACCEPTED
     * message from the server.
     */
    public ChatClient2() {


        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "South");

        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        // Add Listeners
        textField.addActionListener(new ActionListener() {
            /**
             * Responds to pressing the enter key in the textfield by sending
             * the contents of the text field to the server.    Then clear
             * the text area in preparation for the next message.
             */
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });

        }



    /**
     * Prompt for and return the address of the server.
     */
    public void fileTransfer() throws IOException {

        int filesize=1022386;
        int bytesRead;
        int currentTot = 0;
        Socket socket = new Socket("127.0.0.1",6789);
        byte [] bytearray  = new byte [filesize];
        InputStream is = socket.getInputStream();
       FileOutputStream fos = new FileOutputStream("/Users/navinislam/CSCI345/src/main/java/Test/copy.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bytesRead = is.read(bytearray,0,bytearray.length);
        currentTot = bytesRead;

        do {
            bytesRead =
                    is.read(bytearray, currentTot, (bytearray.length-currentTot));
            if(bytesRead >= 0) currentTot += bytesRead;
        } while(bytesRead > -1);

        bos.write(bytearray, 0 , currentTot);
        bos.flush();
        bos.close();
        socket.close();
    }




    private String getServerAddress() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter IP Address of the Server:",
                "Welcome to the Chatter",
                JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Prompt for and return the desired screen name.
     */
    private String getName() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter the correct Screen Name",
                "Enter Username ",
                JOptionPane.PLAIN_MESSAGE);
    }
    private String getPass() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter the correct Password",
                "Enter password",
                JOptionPane.PLAIN_MESSAGE);
    }


    /**
     * Connects to the server then enters the processing loop.
     */
    private void run() throws IOException {

        // Make connection and initialize streams
//        String serverAddress
        String serverAddress= "127.0.0.1";//always connects to same server address

        Socket socket = new Socket(serverAddress, 6789);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                out.println(getName());

            }
            else if (line.startsWith("SUBMITPASS")) {
                out.println(getPass());

            } else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");
            }
        }
    }

    /**
     * Runs the client as an application with a closeable frame.
     */
    public static void main(String[] args) throws Exception {
        ChatClient2 client = new ChatClient2();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
      //  client.fileTransfer();
        client.run();
    }
}