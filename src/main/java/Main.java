import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String... args) throws Exception {
        ServerSocket servidor = new ServerSocket(7878);
        if (!servidor.isBound()){
            servidor.bind(new InetSocketAddress("0.0.0.0", 0));
        }

        try {
            while(true) {
                Socket cliente = servidor.accept();

                ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
                ProcessBuilder builder = new ProcessBuilder("java", "-jar", "spigot.jar");
                Process process = null;
                if(entrada.readUTF().equalsIgnoreCase("start")) {
                    builder.directory(new File("servidor/"));
                    builder.redirectErrorStream(true);
                    process = builder.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
                        saida.flush();
                        saida.writeUTF(line);
                        System.out.println(line);
                    }
                } else if(entrada.readUTF().equalsIgnoreCase("command")) {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                    writer.write(entrada.readObject().toString() + "\n");
                    writer.flush();
                }


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}
