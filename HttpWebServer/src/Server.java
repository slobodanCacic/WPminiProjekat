import java.awt.List;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Server
{
    private int port;
    private ServerSocket serverSocket;
    private static ArrayList<String> brKarte = new ArrayList<String>();
    private static ArrayList<String> imeIprezime = new ArrayList<String>();
    private static ArrayList<String> datumPolaska = new ArrayList<String>();
    private static ArrayList<String> polaziste = new ArrayList<String>();
    private static ArrayList<String> destinacija = new ArrayList<String>();
    private static ArrayList<String> cena = new ArrayList<String>();


    public Server(int port)
            throws IOException
    {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
    }


    public void run()
    {
        System.out.println("Web server running on port: " + port);
        System.out.println("Document root is: " + new File("/static").getAbsolutePath() + "\n");

        Socket socket;

        while (true)
        {
            try
            {
                // prihvataj zahteve
                socket = serverSocket.accept();
                InetAddress addr = socket.getInetAddress();

                // dobavi resurs zahteva
                String resource = this.getResource(socket.getInputStream());
                
                // fail-safe
                if (resource == null)
                    continue;

                if (resource.equals(""))
                    resource = "static/index.html";

                System.out.println("Request from " + addr.getHostName() + ": " +  resource);

                // posalji odgovor
                this.sendResponse(resource, socket.getOutputStream());
                socket.close();
                socket = null;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }


    private String getResource(InputStream is)
            throws IOException
    {
        BufferedReader dis = new BufferedReader(new InputStreamReader(is));
        String s = dis.readLine();

        // fail-safe
        if (s == null)
            return null;

        String[] tokens = s.split(" ");

        // prva linija HTTP zahteva: METOD /resurs HTTP/verzija
        // obradjujemo samo GET metodu
        String method = tokens[0];
        if (!method.equals("GET"))
            return null;

        // String resursa
        String resource = tokens[1];

        // izbacimo znak '/' sa pocetka
        resource = resource.substring(1);

        // ignorisemo ostatak zaglavlja
        String s1;
        while (!(s1 = dis.readLine()).equals(""))
            System.out.println(s1);

        return resource;
    }


    private void sendResponse(String resource, OutputStream os)
            throws IOException
    {
    	
    	
        PrintStream ps = new PrintStream(os);

    //    String splited[] = resource.split("\\?");
	//    String naredba = splited[0];
        resource = resource.replace('/', File.separatorChar);
	    

	    if(resource.equals("Dodaj"))
	    {
	    	ps.print("HTTP/1.0 200 OK\r\n"
		               + "Content-type: text/html; charset=UTF-8\r\n\r\n");
		        
		    HashMap<String,String> parametri = getParameter(resource);
		        
		    for (String key: parametri.keySet()) {
		    	if(key.equals("brojKarte")) {
		    		brKarte.add(parametri.get(key));
		    	}
		    	else if(key.equals("imePrz")) {
		    		imeIprezime.add(parametri.get(key));
		    	}
		    	else if(key.equals("datumPolaska")) {
		    		datumPolaska.add(parametri.get(key));
		    }
		    	else if(key.equals("polaziste")) {
		    		polaziste.add(parametri.get(key));
		    	}
		    	else if(key.equals("destinacija")) {
		    		destinacija.add(parametri.get(key));
		    	}
		    	else if(key.equals("cena")) {
		    		cena.add(parametri.get(key));
		    	}
		    }
        
	    }
	    
	    ps.print("<h1 style=\"color:blue;\">HTTP.Pregled voznih karata</h1>");
        
        ps.print("<form action=\"izmeniid\">");
    	ps.print("<table border=\"1\">");
    	
    	ps.print("<tr><th>#</th><th>Klub</th><th>Bodovi</th><th>Akcije</th></tr>");
	    
	    
        ps.flush();
      
    }
   static HashMap<String, String> getParameter(String requestLine) {
		HashMap<String, String> retVal = new HashMap<String, String>();

		String request = requestLine.split("\\?")[0];
		retVal.put("request", request);
		String parameters = requestLine.substring(requestLine.indexOf("?") + 1);
		StringTokenizer st = new StringTokenizer(parameters, "&");
		while (st.hasMoreTokens()) {
			String key = "";
			String value = "";
			StringTokenizer pst = new StringTokenizer(st.nextToken(), "=");
			key = pst.nextToken();
			if (pst.hasMoreTokens())
				value = pst.nextToken();

			retVal.put(key, value);
		}

		return retVal;
	}

}
