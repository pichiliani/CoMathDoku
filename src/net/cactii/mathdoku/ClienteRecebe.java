package net.cactii.mathdoku;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.awt.*;
import javax.xml.parsers.ParserConfigurationException;

import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



//Estes imports são úteis para a colaboração!
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;

import java.util.Collection;


public class ClienteRecebe extends Thread 
{
	private Socket socket;
	private ObjectInputStream is;
	// private Editor e;
	
	public ClienteRecebe(Socket s,ObjectInputStream i) {
		this.socket = s;
		this.is = i;
	}
	
    public void run ()  {
        
    	try {

            boolean clientTalking = true;
            
        	// Obtendo a view!
            final MainActivity currentView = (MainActivity) MainActivity.getView();
            
            //a loop that reads from and writes to the socket
            while (clientTalking) 
            {
            	
            	//get what client wants to say...
                Object clientObject = this.is.readObject();
                
                ArrayList list = (ArrayList) clientObject;
                
                Object o = list.get(0);
                String nomeEvento = (String)list.get(1);
                
            	
               	if (nomeEvento.equals("PROT_atualiza_modelo_cliente"))
            	{
            	}
                
               	if (nomeEvento.equals("PROT_atualiza_modelo_cliente_inicial"))
            	{
            	}
               	
               	
               	// Recebeu uma mensagem de chat!
               	if (nomeEvento.equals("PROT_chat_msg"))
            	{

            	}

//              Recebeu a notificação que algum cliente entrou na da sessão!
               	if (nomeEvento.equals("PROT_inicio_sessao"))
            	{
            	
               		// Aqui vou fazer um teste para mostrar na tela o que recebi!
               		
               		// final HelloMauroActivity currentView = (HelloMauroActivity) HelloMauroActivity.getView();
               		
               		// final EditText texto = (EditText) currentView.findViewById(R.id.meuTexto);
               		
               		// Toast.makeText(currentView, "Recebi inicio sessão!", Toast.LENGTH_SHORT).show();
               		
               		/*
               	// Do something long
               		Runnable runnable = new Runnable() 
               		{
               			@Override
               			{
		               			currentView.handler.post(new Runnable() 
			               		{
				               		@Override
				               		public void run() 
				               		{
				               			texto.setText("Recebi inicio sessao");
				               		}
			               		});
	               		}
               		};*/
               		
            	}

               	
//              Recebeu a notificação que algum cliente saiu da sessão!
               	if (nomeEvento.equals("PROT_fim_sessao"))
            	{
            	}
               	
                if (nomeEvento.equals("PROT_remove_elemento"))
                {
                
                }
                	
                
                if (nomeEvento.equals("CELL_CHANGE"))
            	{

                	ArrayList lista = (ArrayList) o;
                	
                	final int celula = Integer.parseInt( String.valueOf( lista.get(3) ) );
                	final int cage = Integer.parseInt( String.valueOf( lista.get(5) ) );
                	final int digito = Integer.parseInt( String.valueOf( lista.get(4) ) );
                	
                	// final String dado = String.valueOf( lista.get(4) ) ;
                	
                	
                	// Toast.makeText(currentView, "Recebi_A_TEST!", Toast.LENGTH_SHORT).show();
                	
                	 
                	Runnable runnable = new Runnable() 
               		{
               			@Override
	               		public void run() 
               			{
		               			currentView.handler.post(new Runnable() 
			               		{
				               		@Override
				               		public void run() 
				               		{
				               			// Toast.makeText(currentView, "Celula:" + String.valueOf(celula) + " Digito:" + String.valueOf(digito), Toast.LENGTH_SHORT).show();
				               			currentView.digitSelectedImpl(celula, digito, cage);
				               			
				               		}
			               		});
	               		}
               		};
               		
               		new Thread(runnable).start();
               		
               		
            	}

                
                if (nomeEvento.equals("MT_MOVE"))
            	{
      
            	}
                
                if (nomeEvento.equals("MT_ROTATE"))
            	{
      
            	}
                
                if (nomeEvento.equals("MT_SCALE"))
            	{
                }
                

                if (nomeEvento.equals("MT_TELEFINGER_MOVE"))
            	{
      
            	}
                
                if (nomeEvento.equals("MT_TELEFINGER_HIDE"))
            	{
      
            	}
                
                if (nomeEvento.equals("MT_CREATE_COMPONENT_3D"))
            	{
                }
                

                if (nomeEvento.equals("MT_CREATE_COMPONENT"))
            	{
                }

                
                if (nomeEvento.equals("MT_REMOVE_COMPONENT"))
            	{
            	}
                
                if (nomeEvento.equals("MT_DOUBLE_TAP"))
            	{
            	}

               
                
               	if (nomeEvento.startsWith("SEL_"))
            	{
               		nomeEvento = nomeEvento.substring(4,nomeEvento.length());
               		

                    // TODO: MT4J
               		// SelecionaTool(nomeEvento,o,false);
                    
                    // if(!ProjectManager.getManager().clienteEnvia.AcaoAnterior.equals(""))
                    // {
                    // 	SelecionaTool(ProjectManager.getManager().clienteEnvia.AcaoAnterior,o,true);
                    // }
            	}
               	                
                if (clientObject == null) 
                    clientTalking = false;
            }
           	
            } catch (Exception e) 
            {
            	// Retirado para evitar mostrar o erro da desconexão!
            	e.printStackTrace();
            }    	
    }
    
    
    private void SelecionaTool(String nome_tool,Object o,boolean pinta)  
    {
    /*     

//      Estas ações abaixo fazem o click na barra de ferramentas!
    	UMLDiagram diagram = (UMLDiagram)ProjectManager.getManager().getCurrentProject().getActiveDiagram();
    	
    	Object[] a = diagram.getActions();
        
        // Este loop eh  um exemplo de como setar a ferramenta a ser utilizada
        for(int i=0;i<a.length;i++) {
        	
        	if(a[i] ==null)
        	{
                Globals.curEditor().liberado = true;
        		continue;
        	}
        	
        	// Caso simples: a Action ja estah no array
        	if ( a[i] instanceof RadioAction)
        		if ( ((RadioAction)a[i]).getAction().getValue(Action.NAME).equals(nome_tool) )
        				((RadioAction)a[i]).actionPerformedImpl((java.awt.event.ActionEvent) o,pinta);
        		
        	// Aqui caso tenha clicado em alguma acao que 
        	// esta dentro de um array de objetos
        	if( a[i] instanceof Object[])
        	{
        		Object[] b = (Object[]) a[i];
        		
        		for(int j=0;j<b.length;j++)
        		{
        			if (b[j] instanceof Object[])
        			{
            			Object[] c = (Object[]) b[j];
            		
            			for(int k=0;k<c.length;k++)
                		{
                			if ( c[k] instanceof RadioAction)
                				if ( ((RadioAction)c[k]).getAction().getValue(Action.NAME).equals(nome_tool) )
                        				((RadioAction)c[k]).actionPerformedImpl((java.awt.event.ActionEvent) o,pinta);
                		}
        			}
        		}
        	}
        }
    	*/
    }
    
    private void parseXMI(String dia_xmi)  
    {
    /*	
    	try {
    		
    		// Lendo o XMI
	    	// Estas linhas abaixo são BEM experimentais (mas funcionam)
	    	
	    	XMIReader xmiReader = null;
	        try {
	        	
	            xmiReader = new XMIReader();
	        } catch (SAXException se) { 
	            throw se;
	        } catch (ParserConfigurationException pc) { 
	            throw pc;
	        }
	    	
	        Object mmodel = null;
	
	        StringBufferInputStream sub_XMI = new StringBufferInputStream(dia_xmi);
	        
	        InputSource source = new InputSource(sub_XMI);
	        source.setEncoding("UTF-8");
	        
	        mmodel = xmiReader.parseToModel(source);
	        if (xmiReader.getErrors()) {
	            
	            System.out.println("XMI file could not be parsed."); 
	            throw new SAXException("XMI file  could not be parsed.");
	        }
	        
	        if (xmiReader.getErrors())        {
	        	System.out.println("XMI file  could not be parsed.");
	            throw new SAXException("XMI file  could not be parsed.");
	        }
	
	        UmlHelper.getHelper().addListenersToModel(mmodel);
	
	        HashMap _UUIDRefs = new HashMap(xmiReader.getXMIUUIDToObjectMap());
	        
	        PGMLParser.SINGLETON.setOwnerRegistry(_UUIDRefs);

    	}
    	catch (Exception ee) 
        {
        	System.out.println("Erro na leitura do XMI!");
        	ee.printStackTrace();
        }
        */
    	
    }
    
    /* 
    private ArgoDiagram parsePGML(String dia_pgml)  {
    	
    	// Lendo o PGML
    	
    	StringBufferInputStream sub = new StringBufferInputStream(dia_pgml);

    	ArgoDiagram da =  (ArgoDiagram) PGMLParser.SINGLETON.leDiagram(sub,false);

    	return da;
    }
    */
    
    // O metodo abaixo vai retornar as Fig que existem no diagrama atual
    // mas não existem no diagrama que acabou de chegar
    /*
    private ArrayList RemovidasFigs(ArgoDiagram di_atual,ArgoDiagram di_novo)
    {

    	ArrayList ret = new ArrayList();
    	
    	ArrayList UID_novo = new ArrayList(); 
    	
    	// Primeiro obter todas os  UID's do diagrama novo
    	Layer lay = di_novo.getLayer();
		java.util.List nodes = lay.getContents();

		Iterator ite = nodes.iterator();
		  
		 while (ite.hasNext()) 
		 {
		    Fig f = (Fig)ite.next();
		    
		 	// Ignoro o que for FigPointer
		 	if(     !(f instanceof FigPointer)  &&  !(f instanceof FigEye)   )
		 	{
		 		UID_novo.add(f.classNameAndBounds());
		 	}
		 }
    	
//		 Agora obter as Figs que não existem no diagrama atual
		 lay = di_atual.getLayer();
		 nodes = lay.getContents();

		 ite = nodes.iterator();
			  
		 while (ite.hasNext()) 
		 {
		    Fig f = (Fig)ite.next();
		    
		    if(!UID_novo.contains(f.classNameAndBounds())   && !(f instanceof FigPointer)  && !(f instanceof FigEye))
		    	ret.add(f);
		 }
			 
		 return ret;

		 
    }
    */
    
    // O metodo abaixo vai retornar as novas figuras que não existem no diagrama atual
    /*
    private ArrayList NovasFigs(ArgoDiagram di_atual,ArgoDiagram di_novo)
    {
    	ArrayList ret = new ArrayList();
    	
    	ArrayList UID_atual = new ArrayList(); 
    	
    	// Primeiro obter todas os  UID's do diagrama atual
    	Layer lay = di_atual.getLayer();
    	java.util.List nodes = lay.getContents();
		
    	//  Agora vou procurar o elemento e mudar sua cor
    	Enumeration ite = Globals.curEditor().figs();
		  
		 while (ite.hasMoreElements()) 
		 {
			Fig f = (Fig) ite.nextElement();
			 
		 	// Ignoro o que for FigPointer
		 	if(!(f instanceof FigPointer) && !(f instanceof FigEye)  )
		 	{
			    UID_atual.add(f.classNameAndBounds());
		 	}
		 }
		 
		 // Agora obter as Figs do novo diagrama
		 lay = di_novo.getLayer();
		 nodes = lay.getContents();

		 Iterator ite_novo = nodes.iterator();
			  
		 while (ite_novo.hasNext()) 
		 {
		    Fig f = (Fig) ite_novo.next();
		    
		    if(!UID_atual.contains(f.classNameAndBounds()))
		    	ret.add(f);
		 }
			 
		 return ret;
    }

    */

 /*
    private void MostraDiagrama(ArgoDiagram di,String texto)
    {

        
    	// Vou obter os nós (objetos FigNode)
    	Collection  NosC = di.getNodes(null);
    	
    	Iterator it = NosC.iterator();
    
    	// System.out.println(texto + " - Nos");
    	while (it.hasNext()) 
    	{
            // O objeto vai ser o que FigNode.getOwner() retornar
    		Object fig = it.next();
    		
    		// System.out.println("No - " + fig);
    		// System.out.println("No ItemUID - " + ModelFacade.getUUID(fig) );
            
        }
    	
    	Collection  ArcosC = di.getEdges(null);
    	it = ArcosC.iterator();
    	
    	// System.out.println(texto + " - Arcos");
    	
    	while (it.hasNext()) 
    	{
            // O objeto vai ser o que FigNode.getOwner() retornar
    		Object fig = it.next();
    		System.out.println("Arco - " + fig);
    		// System.out.println("Arco ItemUID - " + ModelFacade.getUUID(fig) );
        }

    	// Neste loop vou tentar pegar os objetos Fig
    	
		Layer lay = di.getLayer();
		java.util.List nodes = lay.getContents();
				
		Iterator ite = nodes.iterator();
		System.out.println(texto + " - Fig");
		  
		 while (ite.hasNext()) 
		 {
		    Fig f = (Fig)ite.next();
		    // System.out.println("Fig: " + f);
		    
		    if( !(f instanceof FigPointer) && !(f instanceof FigEye)  )
		    {
			    System.out.println("Fig Id: " + f.classNameAndBounds());
			    // System.out.println("Fig Outro Id: " + f.getId());
			    // System.out.println("Fig UUID: " + ModelFacade.getUUID(f.getOwner())  );
			    
			
				// This will get all the figs on the current diagram
				// Layer lay = Globals.curEditor().getLayerManager().getActiveLayer()
				// List nodes = lay.getContents();
	
		    }
		  }
      }
      */

/*
    private void AdicionaNoDiagrama(Fig f)
    {
    	// As linhas abaixo adicionam a figura, mas não o elemento no diagrama...
    	// Globals.curEditor().add(f);
    	// Globals.curEditor().damaged(f);
    	
        Object _node;
    	FigNode _pers;

    	// GraphFactory _factory;
    	
    	//1 - Editor.mousePressed()
    		// Fig.mousePressed()
    	
    	//2 - ModeManager.mouseReleased()
    		// Fig.mouseReleased()
    	
    	//3 - ModePlace.mouseClicked()
    		// Fig.mouseClicked()	
    	
    	_node = f.getOwner();
    	// System.out.println("Novo _node:" + _node );
    	
    	// _node = _factory.makeNode();
    	
    	// OUtra maneira de criar o _node eh chamar o makenode()
    	
        Editor editor = Globals.curEditor();
        GraphModel gm = editor.getGraphModel();
        
        GraphNodeRenderer renderer = editor.getGraphNodeRenderer();
        Layer lay = editor.getLayerManager().getActiveLayer();
        // _pers = renderer.getFigNodeFor(gm, lay, _node);

        _pers = (FigNode) f;
        
        int x = f.getX();
        int y = f.getY();

        editor.damageAll();
        Point snapPt = new Point(x, y);
        
        editor.snap(snapPt);

        _pers.setLocation(snapPt.x, snapPt.y);
        
        editor.damageAll();
    	
    	//4 - Editor.mouseReleased()
    	
    	//5- ModeManager.mouseReleased()
    	
    	//6 - ModePlace.mouseReleased()

        gm = editor.getGraphModel();

        MutableGraphModel mgm = (MutableGraphModel)gm;
        if(mgm.canAddNode(_node)) {
        	
        	editor.add(_pers);

        	// Eh necessario setar a namespace
        	((MModelElement) _node).setNamespace(null);
        	
        	// A linha abaixo vai chamar, para o diagrama de classes
        	// O Metodo AddNode da classe ClassDiagramGraphModel 
        	// (ou outra classe que herde de UMLMutableGraphSupport)
            mgm.addNode(_node);
            
            Fig encloser = null;
            Rectangle bbox = _pers.getBounds();
            lay = editor.getLayerManager().getActiveLayer();
            Collection otherFigs = lay.getContents(null);
            
            Iterator it = otherFigs.iterator();
            while(it.hasNext()) {
                Fig otherFig = (Fig)it.next();
                if(!(otherFig instanceof FigNode)) {
                    continue;
                }
                if(otherFig.equals(_pers)) {
                    continue;
                }
                Rectangle trap = otherFig.getTrapRect();
                if(trap != null && (trap.contains(bbox.x, bbox.y) && trap.contains(bbox.x + bbox.width, bbox.y + bbox.height))) {
                    encloser = otherFig;
                }
            }
            
            _pers.setEnclosingFig(encloser);
            if (_node instanceof GraphNodeHooks) {
               ((GraphNodeHooks) _node).postPlacement(editor);
            }
            
            // editor.getSelectionManager().select(_pers);
        }

        editor.setCursor(Cursor.getDefaultCursor());
        
    }*/
    
        
}

