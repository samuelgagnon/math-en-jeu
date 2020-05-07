<?php
/*******************************************************************************
Fichier : admin.class.php
Auteur : Maxime B�gin
Description :
    classe qui h�rite de la classe utilisateur. Elle g�re les administrateurs.
********************************************************************************
15-08-2006 Maxime B�gin - Ajout du cryptage du mot de passe. Utilisation de la 
	fonction password() de MySQL.
11-07-2006 Maxime B�gin - modification des courriels pour qu'il fonctionne avec 
	le fichier de configuration.
21-06-2006 Maxime B�gin - ajout de quelques commentaires
25-05-2006 Maxime B�gin - Version initiale
*******************************************************************************/

require_once("utilisateur.class.php");

class Admin extends Utilisateur
{

    //**************************************************************************
    // Sommaire:        Constructeur, appel du constructeur parent
    // Entr�e:
    // Sortie:
    // Note:
    //**************************************************************************
    function Admin($mysqli)
    {
      PRECONDITION(get_class($mysqli)=="mon_mysqli");
      parent::Utilisateur($mysqli);
      //$this->nbJoueurs=0;
    }
    
    private function INVARIANTS(){}
    
    
    //**************************************************************************
    // Sommaire:    assigner toutes les informations au joueur
    // Entr�e:      $nom
    //              $prenom
    //              $alias
    //              $motDePasse
    //              $courriel
    //              $estConfirmer   : 0 ou 1 pour dire si le courriel de l'administrateur est confir�
    //              $etablissement  : # de l'�tablissement choisie par le joueur
    //              $niveau         : niveau scolaire entre 1 et 14
    // Sortie:
    // Note:        on apelle la focntion parente
    //**************************************************************************
    function asgAdmin($nom,$prenom,$alias,$motDePasse,$courriel,
        $estConfirmer,$etablissement,$niveau)
    {
      parent::asgUtilisateur($nom,$prenom,$alias,$motDePasse,$courriel,
            $estConfirmer,$etablissement,$niveau);
      $this->INVARIANTS();
    }
    
    //**************************************************************************
    // Sommaire:        G�n�rer une cl� unique de confirmation pour le courriel
    // Entr�e:
    // Sortie:
    // Note:            On s'assure que la c� g�n�r�e est unique
    //                  dans la table administrateur avant de l'assign�
    //**************************************************************************
    function genererCleConfirmation()
    {
        //on s'assure que la cle qui servira � la confirmation
        //du courriel soit unique
        do
        {
            $cle = $this->genererChaineAleatoire(30);
            $sql = "select cleConfirmation from administrateur
                where cleConfirmation='" . $cle . "'";
            $result = $this->mysqli->query($sql);

        }
        while($result->num_rows!=0);
        $this->cleConfirmation = $cle;

    }
    
    //**************************************************************************
    // Sommaire:    charger un administrateur de la base de donn�e
    //              MySQL correspondant aux alias et mot de passe en entr�e
    // Entr�e:      $alias
    //              $motDePasse
    // Sortie:      retourn faux si l'utilisateur n'est pas trouv�
    // Note:        
    //**************************************************************************
    function chargerMySQL($alias,$motDePasse)
    {
        $sql="select * from administrateur where alias='$alias' AND motDePasse=password('$motDePasse')";

        $result = $this->mysqli->query($sql);
        if($result->num_rows==0)
            return false;
        else
        {
            $row=$result->fetch_object();
            $this->asgAdmin($row->nom,$row->prenom,$row->alias,$row->motDePasse,
                $row->courriel,$row->estConfirme,$row->cleEtablissement,
                $row->cleNiveau);
            $this->asgCle($row->cleAdministrateur);
            return true;
        }
    }
    
    
    //**************************************************************************
    // Sommaire:    v�rifier que personne n'a d�j� choisie cet alias
    // Entr�e:      $alias
    // Sortie:      retourn faux si l'alias n'est pas unique,vrai sinon
    // Note:        
    //**************************************************************************
    function validerAliasUnique($alias, $mysqli)
    {
        $sql="select * from administrateur where alias='" . $alias . "'";
        $result = $this->mysqli->query($sql);

        if($result->num_rows!=0)
            return false;
        else
            return true;
    }

    //**************************************************************************
    // Sommaire:    v�rifier que personne n'a d�j� choisie ce courriel
    // Entr�e:      $courriel
    // Sortie:      retourne vrai si le courriel est disponible,faux sinon
    // Note:        
    //**************************************************************************
    function validerCourrielUnique($courriel, $mysqli)
    {
        $sql="select * from administrateur where courriel='" . $courriel . "'";
        $result = $this->mysqli->query($sql);

        if($result->num_rows!=0)
            return false;
        else
            return true;
    }

    //**************************************************************************
    // Sommaire:    Ins�rer l'administrateur nouvellement inscrit dans la base de donn�es
    // Entr�e:
    // Sortie:
    // Note:        On doit re-v�rifier que l'alias est unique et
    //              que le courriel est unique
    //**************************************************************************
    function insertionMySQL()
    {
      PRECONDITION($this->validerAliasUnique($this->reqAlias()));
      PRECONDITION($this->validerCourrielUnique($this->reqCourriel()));

      $this->genererCleConfirmation();
        
      $sql = "INSERT INTO administrateur (prenom, nom, alias,
            motDePasse, courriel, cleNiveau, cleConfirmation,
            cleEtablissement) VALUES('";

      $sql .= $this->reqPrenom()."', '"
                .$this->reqNom()."', '"
                .$this->reqAlias()."',password('"
                .$this->reqMotdepasse()."'), '"
                .$this->reqCourriel()."',"
                .$this->reqNiveau().",'"
                .$this->reqCleConfirmation()."',"
                .$this->reqEtablissement().")";

      $result = $this->mysqli->query($sql);
      $this->asgCle($this->mysqli->insert_id);
      $this->envoyerCourrielConfirmation();

    }

    //**************************************************************************
    // Sommaire:    envoyer un courriel de confirmation
    //              pour valider l'adresse courriel
    // Entr�e:
    // Sortie:
    // Note:
    //**************************************************************************
    function envoyerCourrielConfirmation()
    {
		$sujet = SUJET_COURRIEL_INSCRIPTION;
        
        $a_chercher = array('[ALIAS]','[MOT_DE_PASSE]','[NOM]','[PRENOM]','[CLE_CONFIRMATION]','[ADRESSE_SITE_WEB]');
        $remplacement = array($this->reqAlias(),$this->reqMotDePasse(),$this->reqNom(),$this->reqPrenom(),$this->reqCleConfirmation(),ADRESSE_SITE_WEB);
        $message = str_replace($a_chercher,$remplacement,COURRIEL_INSCRIPTION);

        $courriel = new Courriel($sujet,$message,$this->reqCourriel(),COURRIEL_SMAC);
        return $courriel->envoyerCourriel();
    }
    
    
    
    function envoyerCourrielInfoPerdu($courriel)
    {
      if($this->validerCourrielUnique($courriel)==true)
        return false;

        $sql="select * from administrateur where courriel='" . $courriel . "'";
        $result=$this->mysqli->query($sql);
        $row = $result->fetch_object();

        $sujet = SUJET_COURRIEL_PASS_PERDU;
        
        $a_chercher = array('[ALIAS]','[MOT_DE_PASSE]','[NOM]','[PRENOM]','[CLE_CONFIRMATION]','[ADRESSE_SITE_WEB]');
        $remplacement = array($row->alias,$row->motDePasse,$this->reqNom(),$this->reqPrenom(),$this->reqCleConfirmation(),ADRESSE_SITE_WEB);
        $message = str_replace($a_chercher,$remplacement,COURRIEL_PASS_PERDU);

        $courriel = new Courriel($sujet,$message,$courriel,COURRIEL_SMAC);
        $courriel->envoyerCourriel();
        return true;

    }
    
    //**************************************************************************
    // Sommaire:    mettre � jour l'administrateur dans la table
    // Entr�e:
    // Sortie:
    // Note:        on s'assure que le num�ro unique est valide
    //**************************************************************************
    function miseAJourMySQL()
    {
      PRECONDITION($this->reqCle()>=0);
      $sql ="update administrateur set nom='" . $this->reqNom() .
            "',prenom='" . $this->reqPrenom() .
            "',cleNiveau=" . $this->reqNiveau() .
            ",motDePasse='" . $this->reqMotDePasse() .
            "',cleEtablissement=" . $this->reqEtablissement() .
            ",courriel='" . $this->reqCourriel() .
            "',cleConfirmation='" . $this->reqCleConfirmation() .
            "',estConfirme=" . $this->reqEstConfirmer() .
            ",alias='" . $this->reqAlias() . "' where cleAdministrateur=" .
            $this->reqCle();

      $result = $this->mysqli->query($sql);
    }
    
    //**************************************************************************
    // Sommaire:    Mettre � jour le mot de passe avec la fonction password() 
	//				de MySQL
    // Entr�e:
    // Sortie:
    // Note:        
    //**************************************************************************
    function miseAJourMotDePasseMySQL($motDePasse)
    {
      $sql ="update administrateur set motDePasse=password('" . $motDePasse . "') where cleAdministrateur=" . $this->reqCle();
      $result=$this->mysqli->query($sql);
      $this->chargerMySQLCle($this->reqCle());
	  
	}

    //**************************************************************************
    // Sommaire:    supprimer l'administrateur courant
    // Entr�e:
    // Sortie:
    // Note:        on s'assure que le num�ro unique est valide
    //**************************************************************************
    function deleteMySQL()
    {
      PRECONDITION($this->reqCle() >= 0);
      $sql="delete from administrateur where cleAdministrateur=" . $this->reqCle();
      $result = $this->mysqli->query($sql);

    }
}

