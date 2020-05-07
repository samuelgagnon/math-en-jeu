<?php
/*******************************************************************************
Fichier : ini.php
Auteur : Maxime B�gin
Description : regroupe toutes les fonctions d'initialisation

********************************************************************************
10-11-2006 Maxime B�gin - Ajout de constante pour la connexion � un serveur SMTP
	pour l'envoie de courriel
26-07-2006 Maxime B�gin - modification de DOC_ROOT en constante et non plus en
	variable globale.
04-07-2006 Maxime B�gin - Ajout d'un fichier xml pour les configurations
21-06-2006 Maxime B�gin - Ajout de commentaires.
30-05-2006 Maxime B�gin - Version initiale
*******************************************************************************/
ini_set('display_errors','1');
//d�finir les configurations de session
ini_set('session.gc_maxlifetime',1800);
//ini_set('session.cookie_lifetime',1800);
ini_set('session.use_trans_sid',1);
//ini_set('session.use_only_cookies',1);
//ini_set('session.auto_start',true);


define("SQL_DEBUG",1);      //mettre en commentaire pour passer au mode release
define("CONTRAT_DEBUG",1);  //mettre en commentaire pour passer au mode release

//on cherche le dossier racine 
$chemin = strtr(__FILE__,'\\',"/");
define('LIB_DIR',substr($chemin,0,strrpos($chemin,'/')));
define('DOC_ROOT',(substr(LIB_DIR,0,strrpos(LIB_DIR,'/')+1)));		//dossier racine du site web ( ex : /var/www/html/mathenjeu )
define('LOG_DIR',DOC_ROOT . "log/");

//inclus les fichier requis pour faire fonctionner le site web
//
require_once(LIB_DIR . "/phpmailer/class.phpmailer.php");
require_once(LIB_DIR . "/util.php");
require_once(LIB_DIR . "/exception.class.php");
require_once(LIB_DIR . "/mon_mysqli.class.php");
require_once(LIB_DIR . "/utilisateur.class.php");
require_once(LIB_DIR . "/joueur.class.php");
require_once(LIB_DIR . "/admin.class.php");
require_once(LIB_DIR . "/dao.php");
require_once(LIB_DIR . "/nouvelles.class.php");
require_once(LIB_DIR . "/sondage.class.php");
require_once(LIB_DIR . "/groupe.class.php");
require_once(LIB_DIR . "/superadmin.class.php");
require_once(LIB_DIR . "/faq.class.php");
require_once(LIB_DIR . "/clog.class.php");
require_once(LIB_DIR . "/mon_smarty.class.php");

define("LANGAGE_DIR",DOC_ROOT . "langage/");
define("CONFIG_FILE", DOC_ROOT . "/config/configuration.xml");
$config = simplexml_load_file(CONFIG_FILE);

//require_once(LANGAGE_DIR . $config->langue . "/lang_main.php");

define("MAX_NB_NOUVELLES",(int)$config->nbNouvelles);       	//nombre maximal de nouvelles � afficher
define("MAX_NB_JOUEURS_PALMARES",(int)$config->nbJoueurs);   	//nombre de joueurs pour les palmar�s
define("MIN_NB_PARTIE_PALMARES",(int)$config->minParties);     	//nombre de partie minimal pour les palmar�s
define("NB_JOUR_PALMARES",(int)$config->nbJours);          		//nombre de jour dans le calcul des palmar�s
define("ADRESSE_SITE_WEB",(string)$config->adresseWeb);			//"http://www.smac.ulaval.ca/mathenjeu/jeu_version2/");
define('TEMPLATES_DIR',DOC_ROOT . "templates/");				//dossier des templates
define('TEMPLATE','templates/' . (string)$config->template . "/");  	//dossier du template utilis�
define('TEMPLATE_DIR',DOC_ROOT . "/" . TEMPLATE);				//chemin complet du template utilis�
define('CSS_FILE',(string)$config->css);						//fichier css utilis�
define("FLASH_DIR", ADRESSE_SITE_WEB . "flash/");

define("NOM_COURRIEL",(string)$config->nomCourriel);			//le nom utilis� lorsqu'on envoie des courriels
define("ADRESSE_COURRIEL",(string)$config->courriel);			//l'adresse de courriel utilis�
define("PORT_SMTP",(string)$config->portSMTP);					//le port du serveur SMTP
define("SERVEUR_SMTP",(string)$config->serveurSMTP);			//l'adresse du serveur SMTP
define("USER_SMTP",(string)$config->utilisateurSMTP);			//le nom d'utilisateur du serveur SMTP
define("PASS_SMTP",(string)$config->motDePasseSMTP);			//le mot de passe du serveur SMTP

define('SUJET_COURRIEL_INSCRIPTION',(string)utf8_decode((string)$config->sujet_courriel_inscription));	//sujet du courriel de l'inscription
define('COURRIEL_INSCRIPTION',(string)utf8_decode((string)$config->courriel_inscription));				//courriel envoy� lors de l'inscription
define('SUJET_COURRIEL_PASS_PERDU',(string)utf8_decode((string)$config->sujet_courriel_pass_perdu));	//sujet du courriel de r�cup�ration de mot de passe
define('COURRIEL_PASS_PERDU',(string)utf8_decode((string)$config->courriel_pass_perdu));				//courriel envoy� pour les nom d'usager ou les mot de passe perdu

//constante utile pour la cr�ation des questions
define("TEMP_DIR",(string)$config->tempDir);					//temporary dir for the questions creation
define("LATEX",(string)$config->latexApp);						//latex application command
define("FICHIER_XSL",(string)$config->xslFile);					//xsl fil to be used with xalan
define("XALAN_JAR",(string)$config->xalanApp);					//xalan jar file
define("HEADER_LATEX",(string)$config->latexHeader);			//latex header
define("FOOTER_LATEX",(string) $config->latexFooter);			//latex footer
define("QUESTION_EPS_DIR",(string)$config->epsDir);				//eps/ps dir
define("QUESTION_FLASH_DIR",(string)$config->flashDir);			//flash dir
define("QUESTION_EPS_WEB_DIR",(string)$config->epsWebDir);		//eps/ps web dir
define("QUESTION_FLASH_WEB_DIR",(string)$config->flashWebDir);	//flash web dir

define("IMAGE_DIR",DOC_ROOT . "img/sujet");						//dossier des images pour les nouvelles
define("LOG_FILE",DOC_ROOT . "/log/log.txt");					//fichier pour les logs


//define("LANG_FRENCH",0);
//define("LANG_ENGLISH",1);



if (session_id() == "") {
  //start the session
  session_start();
  
}


//echo $_SESSION['langage'];

if (isset($_SESSION['langage']) && $_SESSION['langage'] != "" ) {
  require_once(LANGAGE_DIR . $_SESSION['langage'] . "/lang_main.php");
} else {
  require_once(LANGAGE_DIR . (string)$config->langue . "/lang_main.php");
  $_SESSION['langage'] = (string)$config->langue;
}




//si l'objet mon_mysqli n'existe pas on le cr�e et l'ajoute
//� la super-globale $_SESSION
//if(!isset($_SESSION["mysqli"]))
//{
    
//}

/*
if (!isset($_SESSION["mysqli"])) {
  $mysqli=new mon_mysqli((string)$config->dbHote,
                (string)$config->dbUtilisateur,
                (string)$config->dbMotDePasse,
                (string)$config->dbSchema);
    
  $_SESSION["mysqli"]=$mysqli;
}
*/
if(isset($_SESSION["mysqli"])) {
  $_SESSION["mysqli"]->close();
}

  $mysqli=new mon_mysqli((string)$config->dbHote,
                (string)$config->dbUtilisateur,
                (string)$config->dbMotDePasse,
                (string)$config->dbSchema);
    
  $_SESSION["mysqli"]=$mysqli;
  
/*
if(isset($_SESSION["mysqli"])) {
  $_SESSION["mysqli"]->close();
}

$mysqli=new mon_mysqli((string)$config->dbHote,
                (string)$config->dbUtilisateur,
                (string)$config->dbMotDePasse,
                (string)$config->dbSchema);
    
$_SESSION["mysqli"]=$mysqli;
*/
//session_destroy();




