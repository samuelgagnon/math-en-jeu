<?php
/*******************************************************************************
Fichier : mon_mysqli.class.php
Auteur : Maxime B�gin
Description :
    extension de la classe mysqli pour automatiquement se connect�
    � la base de donn�es
********************************************************************************
05-06-2006 Maxime B�gin - Version initiale
21-06-2006 Maxime B�gin - ajout de quelques commentaires
*******************************************************************************/

require_once("exception.class.php");

class mon_mysqli extends mysqli
{
    private $dbHote;
    private $dbUtilisateur;
    private $dbMotDePasse;
    private $dbSchema;

    //**************************************************************************
    // Sommaire:        Constructeur avec connexion � la BD
    // Entr�e:          $hote : le serveur hote
    //                  $utilisateur : le nom d'utilisateur
    //                  $motDePasse : le mote de passe
    //                  $schema : le nom de la base de donn�es
    // Sortie:
    // Note:
    //**************************************************************************
    function mon_mysqli($hote,$utilisateur,$motDePasse,$schema)
    {
      global $lang;
      $this->dbHote=$hote;
      $this->dbUtilisateur=$utilisateur;
      $this->dbMotDePasse=$motDePasse;
      $this->dbSchema=$schema;
      parent::__construct($hote,$utilisateur,$motDePasse,$schema);
    }

    //**************************************************************************
    // Sommaire:        red�finition de la fonction query pour g�n�rer un exception
    //                  lorsque que la requ�te est invalide.
    // Entr�e:          $requete : la requete sql � ex�cuter
    //
    // Sortie:
    // Note:            g�n�re une exception de type sql si une erreur survient
    //**************************************************************************
    function query($requete)
    {
        $result = parent::query($requete);
        if(mysqli_error($this))
        {
         	$log = new clog(LOG_FILE);
         	$log->ecrire(mysqli_error($this) . "\r\n" . $requete . "\n");
         	//$log->ecrire(mysqli_error($this)->file . "\n");
         	//$log->ecrire(mysqli_error($this)->line . "\n");
			//$log->ecrire($requete . "\n");

         	if(defined('SQL_DEBUG'))
         	{
            	throw new SQLException(mysqli_error($this), mysqli_errno($this), $requete);
            }
        }

        return $result;
    }

    //**************************************************************************
    // Sommaire:        retourne la liste des nom d'object qui doivent �tre conserv�
    // Entr�e:          
    // Sortie:
    // Note:
    //**************************************************************************
    function __sleep()
    {
        return array_keys(get_class_vars(get_class($this)));
    }

    //**************************************************************************
    // Sommaire:        s'assure de recr�er la connexion � la base de donn�e
    // Entr�e:
    // Sortie:
    // Note:
    //**************************************************************************
    function __wakeup()
    {
      if($this->dbHote!="")
        $this->mon_mysqli($this->dbHote,
                        $this->dbUtilisateur,
                        $this->dbMotDePasse,
                        $this->dbSchema);
    }
    

}


