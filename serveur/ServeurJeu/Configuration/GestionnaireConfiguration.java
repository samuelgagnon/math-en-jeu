package ServeurJeu.Configuration;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class GestionnaireConfiguration 
{
	private static GestionnaireConfiguration _instance = null;
	private XMLConfiguration  _config = null;
	private static final String _FICHIER_CONFIG = "mathenjeu.xml";
	
	private GestionnaireConfiguration()
	{
		init();
	}
	
	private void init()
	{	
		try 
		{
			_config = new XMLConfiguration( _FICHIER_CONFIG );
		} 
		catch (ConfigurationException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static GestionnaireConfiguration obtenirInstance()
	{
		if( _instance == null )
		{
			_instance = new GestionnaireConfiguration();
		}
		return _instance;
	}
	
	public int obtenirNombreEntier( String id )
	{
		return _config.getInt( id );
	}
	
	public String obtenirString( String id )
	{
		return _config.getString( id );
	}
}