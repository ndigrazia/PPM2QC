	package entor.qc.services;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

public class ReleasesServicios {

	private static final String SEPARATOR_QC_PROYECT = " || ";
	
	private static final String SEPARATOR_QC_RELEASE = " - ";
	
	private static final String SEPARATOR_PATH_FOLDERS = "\\";
	
    private static final String INBOX_NAME = "inbox";
    
    private static final String ID_PPM_FIELD = "REL_USER_02";
    
    private static final String FECHA_COMPROMETIDA_FIELD = "REL_USER_04";
    
    private static final String ID_PARENT_RELEASE_FOLDER_FIELD = "RF_PARENT_ID";
    
    private static final String ID_RELEASE_FOLDER_FIELD = "RF_ID";
    
    private static final String NAME_RELEASE_FOLDER_FIELD = "RF_NAME";

    private QcConnection connection;
    
	public ReleasesServicios(QcConnection con) {
		connection = con;
	}
	
	public int addProyectToInboxFromPPM(String idProyect, String titleProyect, String typeProyect, String idOt, String titleOt,
			Date inicioFecha, Date finalFecha, Date fechaComprometida) {
	 
		ActiveXComponent inbox = getInboxFolder();
		
		String qcProyect = idProyect + SEPARATOR_QC_PROYECT + titleProyect;
		String qcReleaseProyect = qcProyect +  SEPARATOR_QC_PROYECT + typeProyect + " " + idOt + SEPARATOR_QC_RELEASE + titleOt;
			
		ActiveXComponent sfp = getFolderByPath(INBOX_NAME + SEPARATOR_PATH_FOLDERS + qcProyect);
		
		if(sfp==null)
			sfp = agregarSubFolderInFolder(inbox, qcProyect);
		
		ActiveXComponent r = agregarReleaseInFolder(sfp, qcReleaseProyect, idProyect, inicioFecha, finalFecha, fechaComprometida);
		
		addTemplateCicle(r);
		
		return r.getProperty("ID").getInt();
	}
	
	private ActiveXComponent getInboxFolder() {
		
		ActiveXComponent inbox = getFolderByPath(INBOX_NAME);
	    
	    if(inbox==null)
	    	inbox = agregarSubFolderInRoot(INBOX_NAME);
	    
	    return inbox;
	    
	}
		
    public void addProyectToInbox(String nameProyect, String idPPM, Date inicioFecha, Date finalFecha, Date fechaComprometida) {
    	
     	ActiveXComponent inbox = getInboxFolder();
	    
	    ActiveXComponent sfp = agregarSubFolderInFolder(inbox, nameProyect);
        ActiveXComponent r = agregarReleaseInFolder(sfp, nameProyect, idPPM, inicioFecha, finalFecha, fechaComprometida);
        
        addTemplateCicle(r);
        
    }  
    
    private void addTemplateCicle(ActiveXComponent release) {
    	
    	agregarCicloEnRelease(release,"1. ACEPTACIÓN DE ENTREGA", null, null);
    	agregarCicloEnRelease(release,"2. EJECUCIÓN", null, null);
    	agregarCicloEnRelease(release,"3. REGRESIÓN", null, null);
    	agregarCicloEnRelease(release,"4. UAT", null, null);
    	agregarCicloEnRelease(release,"5. PERFORMANCE", null, null);
         
    }
    
    public void agregarCicloEnRelease(ActiveXComponent release, String nombre, Date inicioFecha, Date finalFecha) {
    	
    	ActiveXComponent cicloFactory = release.getPropertyAsComponent("CycleFactory");
    	
    	ActiveXComponent ciclo = cicloFactory.invokeGetComponent("AddItem", createNullVariant());
        ciclo.setProperty("Name", nombre);

		if(inicioFecha!=null) {
       	 	Variant iVariant = new Variant();
        	iVariant.putDate(inicioFecha);
        	ciclo.setProperty("StartDate", iVariant);
       	}
       
       	if(finalFecha!=null) {
        	Variant fVariant = new Variant();
        	fVariant.putDate(finalFecha);
        	ciclo.setProperty("EndDate", fVariant);
        }
        
        ciclo.invoke("post");
    	
    }
        
    public ActiveXComponent agregarReleaseInFolder(ActiveXComponent folder, String nombre, 
    		String idPPM, Date inicioFecha, Date finalFecha, Date fechaComprometida) {
    
    	ActiveXComponent releaseFactory = folder.getPropertyAsComponent("ReleaseFactory");

        ActiveXComponent release = releaseFactory.invokeGetComponent("AddItem", createNullVariant());
        release.setProperty("Name", nombre);

        if(fechaComprometida == null) {
        	fechaComprometida = GregorianCalendar.getInstance().getTime(); //HOY
        }
        
        Variant fcVariant = new Variant();
        fcVariant.putDate(fechaComprometida);
                
        Dispatch.invoke(release, "Field", 4, new Object[]{ID_PPM_FIELD, getEncodedString(idPPM)}, new int[2]);
        Dispatch.invoke(release, "Field", 4, new Object[]{FECHA_COMPROMETIDA_FIELD , fcVariant}, new int[2]);

		if(inicioFecha == null) {
			inicioFecha = GregorianCalendar.getInstance().getTime(); //HOY
        }
        	
		Variant lVariant = new Variant();
    	lVariant.putDate(inicioFecha);
    	release.setProperty("StartDate", lVariant);
        
        if(finalFecha!=null) {
        	Variant fVariant = new Variant();
        	fVariant.putDate(finalFecha);
        	release.setProperty("EndDate", fVariant);
        }
        
        release.invoke("post");
        
        return release;
        
    }
        
    public ActiveXComponent agregarSubFolderInFolder(ActiveXComponent folder, String nombreSubFolder) {

    	ActiveXComponent releaseFactory = folder.getPropertyAsComponent("ReleaseFolderFactory");

        ActiveXComponent subFolder = releaseFactory.invokeGetComponent("AddItem", createNullVariant());
      
        subFolder.setProperty("Name", nombreSubFolder);
  		subFolder.invoke("post");
  
        return subFolder;
        
    }
    
    public ActiveXComponent agregarSubFolderInRoot(String nombreSubFolder) {
    
    	ActiveXComponent root = getReleaseFolderFactory().getPropertyAsComponent("Root");
    	return agregarSubFolderInFolder(root, nombreSubFolder);
    	
    }
    
    public ActiveXComponent getFolderByPath(String path) {
    	
    	ActiveXComponent father = getReleaseFolderFactory().getPropertyAsComponent("Root");
    	StringTokenizer st = new StringTokenizer(path, SEPARATOR_PATH_FOLDERS);
		while(st.hasMoreTokens()) {
			String subFolder = st.nextToken();
		   	father = getSubFolderByName(father, subFolder);
        	if(father==null) 
        		break;
		}
    	
		return father;
		    	
    }
        
    public ActiveXComponent getSubFolderByName(ActiveXComponent folder, String nameSubFolder) {
    	
    	ActiveXComponent list = getSubFoldersByParent(folder);
    	int cant = list.getPropertyAsInt("count");
       	
       	ActiveXComponent value = null;
       	
        for(int x=1; x<=cant;x++) {
        		ActiveXComponent item = list.invokeGetComponent("Item", new Variant(x));
             	if(item.getPropertyAsString("name").equals(nameSubFolder)) {
             		value = item;
             		break;
             	}
        }
        
        return value;
         
    }
    
  	public ActiveXComponent getSubFoldersByParent(ActiveXComponent folder) {
    	
        ActiveXComponent filter = getReleaseFolderFactory().getPropertyAsComponent(("Filter"));
        Dispatch.invoke(filter, "Filter", 4, new Object[]{ID_PARENT_RELEASE_FOLDER_FIELD, folder.getPropertyAsInt("ID")}, new int[2]);
        Dispatch list = Dispatch.call(filter, "NewList").getDispatch();
        ActiveXComponent lisC = new ActiveXComponent(list);
        
        return lisC;
        
    }
    
  	private QcConnection getQcConnection() {
        if (connection != null)
        	return connection;
        else
        	throw new RuntimeException("No connection to Quality Center");
    }
    
    public void releaseConnection(){
    	connection = null;
    }

  	//todo: FUNCIONA SOLO SI EL RELEASE ESTÁ EN EL ROOT
    public void borrarRelease(int idQC) {
    	
        ActiveXComponent root = getReleaseFolderFactory().getPropertyAsComponent("Root");
        ActiveXComponent releaseFactory = root.getPropertyAsComponent("ReleaseFactory");

        Variant lVariant = new Variant();
        lVariant.putInt(idQC);
        releaseFactory.invoke("RemoveItem", lVariant);
        
    }

    //Ejemplo de filtro: "*TRAD*"
    public void buscarCarpetasEImprimir(String filtro) {
    	
        ActiveXComponent filter = getReleaseFolderFactory().getPropertyAsComponent(("Filter"));
        Dispatch.invoke(filter, "Filter", 4, new Object[]{NAME_RELEASE_FOLDER_FIELD, getEncodedString(filtro)}, new int[2]);
        Dispatch list = Dispatch.call(filter, "NewList").getDispatch();
        ActiveXComponent lisC = new ActiveXComponent(list);
        int cnt = lisC.getPropertyAsInt("count");
        System.out.println("Cantidad : " + cnt);
        
        for(int x=1; x<=cnt;x++) {
            ActiveXComponent item = lisC.invokeGetComponent("Item", new Variant(x));
            System.out.println("name :" + item.getPropertyAsString("name"));
            System.out.println("ID: " + item.getPropertyAsInt("ID"));
            ActiveXComponent parent = item.getPropertyAsComponent("Parent");
            System.out.println("ParentName: " + parent.getPropertyAsString("name"));
            System.out.println("ParentID: " + parent.getPropertyAsInt("ID"));
        }
        
    }

    public ActiveXComponent getFolder(long id) {
    	
        ActiveXComponent filter = getReleaseFolderFactory().getPropertyAsComponent(("Filter"));
        Dispatch.invoke(filter, "Filter", 4, new Object[]{ID_RELEASE_FOLDER_FIELD, id}, new int[2]);
        Dispatch list = Dispatch.call(filter, "NewList").getDispatch();
        ActiveXComponent lisC = new ActiveXComponent(list);
        int cant = lisC.getPropertyAsInt("count");
        if (cant == 0){
            System.out.println("Carpeta no encontrada, id = " + id);
            return null;
        }
        if (cant > 1){
            //CREO QUE NO PUEDE PASAR ESTO
            System.out.println("Se encontró más de una carpeta con el mismo id, id = " + id);
            return null;
        }

        return  lisC.invokeGetComponent("Item", new Variant(1));
        
    }

    public void agregarReleaseEnRoot(String nombre, String idPPM, Date inicio) {

        ActiveXComponent root = getReleaseFolderFactory().getPropertyAsComponent("Root");
        agregarReleaseInFolder(root, nombre, idPPM, inicio, null, null);
   
   	}

    public void agregarRelease(long folderIdParent, String nombre, String idPPM, Date inicio) {

        ActiveXComponent parent = getFolder(folderIdParent);
        agregarReleaseInFolder(parent, nombre, idPPM, inicio, null, null);

    }

    private String getEncodedString(String pString) {
    	
    	if (pString.startsWith("'")) {
    		return pString;
	    } else {
	    	return pString.indexOf(' ') == -1 ? pString : (new StringBuilder("'")).append(pString).append("'").toString();
	    }
    	
	}

	private Variant createNullVariant() {
		
		Variant lVariant = new Variant();
		lVariant.putNull();
		
		return lVariant;
		
	}
	
	private ActiveXComponent getReleaseFolderFactory(){
		return getQcConnection().getConnection().getPropertyAsComponent("ReleaseFolderFactory");
    }

}
