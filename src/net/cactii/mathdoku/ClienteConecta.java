package net.cactii.mathdoku;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.ParserConfigurationException;

import android.graphics.*;


//Estes imports são úteis para a colaboração!
import java.net.*;
import java.io.*;
import java.util.*;

public class ClienteConecta extends Thread 
{
	
    private String serverName;
    private int serverPort;
    private String lo; //login
    private String pass; //senhs
    
    private Socket socket;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    public boolean connected;
    private ClienteRecebe cr;
    public Vector objAenviar;         // vector of currently connected clients
    public Vector objFila = new Vector();
    
    public Vector listaSessoes;  
	public String AcaoAnterior = "";
	
	private String cor_telepointer;
	private String id_telepointer = "";

    public void EnviaEvento(Object me,String evento)
    {
    	if(connected) 
    	{

			ArrayList list = new ArrayList();
	    	list.add(me); // objeto 'genérico' (pode ser um mouse event ou outro)
        	list.add(evento); // o nome do evento
        	// list.add(_modeManager.getModes().clone()); // o vetor de modes
        	// list.add(this.getModeManager().getModes());
        
        	this.objAenviar.add(list);
    	}

    }


    public void EnviaEventoAtraso(Object me,String evento)
    {
    	if(connected) 
    	{
    	
    		
    		ArrayList list = new ArrayList();
	    	list.add(me); // objeto 'genérico' (pode ser um mouse event ou outro)
        	list.add(evento); // o nome do evento
        	// list.add(_modeManager.getModes().clone()); // o vetor de modes
        	// list.add(this.getModeManager().getModes());
        
        	this.objFila.add(list);
        	
        	// Setar que enviou um SEL_
        	
            // TODO: MT4J
        	// Globals.curEditor().clienteEnvia.EventosArgo = true;
        	
    	}

    }
    
    public void EnviaAtraso()
    {
    
    	// Neste método são enviados todos os
    	// eventos pendentes do argo.
    	 for (int i = 0; i < objFila.size(); i++) 
    	 {
    		 this.objAenviar.add( objFila.elementAt(i)  );
    	 }
    	 
    	 objFila.clear();
    	 
     // TODO: MT4J
     // Globals.curEditor().clienteEnvia.EventosArgo = false; 
    }
    
    
    
	public ClienteConecta() {
        this.connected = false;
        this.objAenviar = new Vector();

	}
	
	public String getLogin()
	{
		return this.lo;
		
	}
	

	public void setaCorTelepointer(String c)
	{
		cor_telepointer = c;
	}
	
	public String getCorTelepointer()
	{
		return cor_telepointer;
	}

	public void setaIdTelepointer(String c)
	{
		id_telepointer = c;
	}
	
	public String getIdTelepointer()
	{
		return id_telepointer;
	}
	
	public boolean SetaUser(String login,String senha) 
	{
        try 
		{
			this.lo = login;
	        this.pass = senha;
	    
	        
	        // O primeiro objeto a ser enviado é uma string que vai indicar
	        // ao servidor se eh uma conexão para trocas de objetos no nível do 
	        // GEF ou nível do ArgoUML. Devo enviar também o login e a senha
	    	ArrayList l = new ArrayList();
	      	l.add(this.lo); // 
	        l.add(this.pass); // 
	        
	        this.EnviaEvento(l,"ARGO");

	        this.os.writeObject((Object) objAenviar.elementAt(0));
            this.os.flush();
            this.os.reset();
            
            objAenviar.clear();
			
            // Recebendo a resposta
	        Object clientObject = this.is.readObject();
            
            ArrayList list = (ArrayList) clientObject;
	
            Object o = list.get(0);
            String nomeEvento = (String)list.get(1);
	
	    	if (nomeEvento.equals("ERRO"))
	    	{
	    		// TODO: MT4J
                // JOptionPane.showMessageDialog(ProjectBrowser.getInstance(),"Login ou senha incorretos!","Erro de conexão",JOptionPane.ERROR_MESSAGE);
	    		// ProjectManager.getManager().clienteEnvia.connected = false;
	    	
	    		return false;
	    	}
	    	else
	    	{
	    		// Aqui vou armazenar as informações que vão ser colocadas na tabela!

            	if (nomeEvento.equals("PROT_lista_sessoes"))
            	{
            		Vector se = (Vector) o;
            		// Colocando os nomes das sessões colaborativas
            		
            		this.listaSessoes = se; 

            		// Agora colocando a cor do telepointer
            		String id = (String) list.get(2);
            		String c = (String) list.get(3);
            		/* 
            		Color fColor = Color.BLACK;
            		if (id.equals("1")) 
            			fColor = Color.RED; 
            		if (id.equals("2")) 
            			fColor = Color.BLUE;
            		if (id.equals("3")) 
            			fColor = Color.YELLOW;
            		if (id.equals("4")) 
            			fColor = Color.LIME;
            		*/
            		
                    this.setaCorTelepointer(c);
            		this.setaIdTelepointer(id);
    	        	
            	} 

            	// Iniciando a Thread que vai receber os dados
                this.cr = new ClienteRecebe(socket,is);

                this.cr.start();

	            
	    		return true;
	    	}
		}
        catch (Exception e) 
		{
        	e.printStackTrace();
        	return false;
        }
        
	}
	
	
	public boolean SetaConecta(String s,int port) 
	{
        
		if(!this.connected)
		{
			this.serverName = s;
	        this.serverPort = port;
	        
	        return  connect();
		}
		else
			return true;
	}
      
    protected boolean connect () {
        try {
            if(socket != null) {
                try {
                    os.close();
                } catch (Exception e) {}
                try {
                    is.close();
                } catch (Exception e) {}
                try {
                    socket.close();
                } catch (Exception e) {}
            }
            
            
            InetAddress end = getHostAddress(serverName); 
            
            // socket = new Socket(serverName, serverPort);
            
            socket = new Socket(end, serverPort);
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
            connected = true;
            
             return true;
            
        } catch (Exception e) {
        	
            // TODO: MT4J
            // JOptionPane.showMessageDialog(ProjectBrowser.getInstance(),"Erro de conexão com o servidor de colaboração","Erro de conexão",JOptionPane.ERROR_MESSAGE);
        	e.printStackTrace();
        	connected = false;
        	return false;
        	
        }
    }
    
    public void run ()  {
    
        try {
          

            Object envia;
            
            while(true) {
            	
                for (int i = 0; i < objAenviar.size(); i++) {
                	envia = (Object) objAenviar.elementAt(i);
                	
                    this.os.flush();
                    this.os.reset();

                    this.os.writeObject(envia);
                    this.os.flush();
                    // this.os.reset();
                    
                }
                
                // Limpando o vetor de itens enviados
                objAenviar.clear();
                
                // Esperando alguns milisegundos
                this.sleep(100);
            }
        
        } catch (Exception e) 
        {
        	e.printStackTrace();
        }    	
        disconnect();
    	
    }

    public void disconnect () {
        try {
            
        	this.sleep(3000);
        	this.is.close();
            this.os.close();
            //close the socket of the server with this specific client.
            this.socket.close();
            connected = false;

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    private InetAddress getHostAddress(String host) throws UnknownHostException {
        String[] oct  = host.split("\\.");
        
        byte[] val  = new byte[4];
        for(int count = 0; count < 4; count++) {
            val[count] = (byte) Integer.parseInt(oct[count]);
        }
        InetAddress addr = InetAddress.getByAddress(host, val);
        return addr;
    }

    
}


