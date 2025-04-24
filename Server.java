import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Server extends JFrame {

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    // Components of the server GUI
    private JLabel heading = new JLabel("Server");
    private JTextArea chat_box_area = new JTextArea();
    private JTextField text_area = new JTextField();
    private Font font = new Font("Arial", Font.PLAIN, 20);

    // Constructor
    @SuppressWarnings("CallToPrintStackTrace")
    public Server() {
        try {
            server = new ServerSocket(7778);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting for client request...");
            socket = server.accept(); // establishes connection between client and server

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents(); // handle events for the GUI

            startReading();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEvents() {
        text_area.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String contentToSend = text_area.getText();
                    chat_box_area.append("Me: " + contentToSend + "\n"); // append the message to the chat box area
                    out.println(contentToSend);
                    out.flush();
                    text_area.setText(""); // clear the text area after sending the message
                    text_area.requestFocus(); // set focus back to the text area
                }
            }
        });
    }

    private void createGUI() {
        // code for graphical user interface
        this.setTitle("Server Messenger");
        this.setSize(700, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // components
        heading.setFont(font);
        chat_box_area.setFont(font);
        text_area.setFont(font);


        ImageIcon icon = new ImageIcon("server.png");
        Image img = icon.getImage(); // Get the original image
        Image resizedImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Resize to 50x50 (change as needed)
        ImageIcon resizedIcon = new ImageIcon(resizedImg);

        heading.setIcon(resizedIcon);// set icon to the label
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        

        // Set the chat box area part of the chat GUI
        chat_box_area.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
        chat_box_area.setEditable(false); // make the chat box area non-editable
        // Set the text area part of the chat GUI
        text_area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        text_area.setPreferredSize(new java.awt.Dimension(text_area.getWidth(), 45));
        text_area.setBackground(Color.LIGHT_GRAY);
        text_area.setForeground(Color.BLACK);

        // Frame Layout
        this.setLayout(new BorderLayout());

        // adding components to frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(chat_box_area); // Add a scroll pane to the chat box area
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(text_area, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void startReading() {
        // Read the thread
        Runnable r1 = () -> {
            System.out.println("reader started....");
            try {
                while (true) {
                    String message = br.readLine();
                    if (message == null || message.equals("exit")) {
                        JOptionPane.showMessageDialog(this, "Client terminated the chat. Connection closed.");
                        text_area.setEnabled(false); // disable the text area
                        socket.close();
                        break;
                    }
                    chat_box_area.append("Client: " + message + "\n"); // append the message to the chat box area
                }
            } catch (IOException ex) {
                System.out.println("Connection closed.");
            }
        };
        new Thread(r1).start(); // start the thread
    }

    public void startWriting() {
        // Write the thread, and send to client.
        Runnable r2 = () -> {
            System.out.println("writer started....");
            try {
                while (!socket.isClosed()) {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if (content.equals("exit")) {
                        JOptionPane.showMessageDialog(this, "Server has exited the chat. Connection closed.");
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is server... server is starting...");
        Server serverInstance = new Server(); // Create an instance of the Server class to start the server
    }
}

