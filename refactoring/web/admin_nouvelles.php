<?php
require_once("lib/ini.php");

main();

/*******************************************************************************
Fonction : main()
Param�tre : -
Description : permet de g�rer les diff�rentes actions � effectuer
*******************************************************************************/
function main()
{
  $smarty = new MonSmarty($_SESSION['langage']);
  global $lang;
  try
  {

	if(isset($_SESSION["joueur"]))
	{
	 	//v�rifie que l'utilisateur peut �tre ici
	 	if($_SESSION["joueur"]->reqCategorie()<5)
	 	{
			redirection('index.php',0);
			return;
		}
		$smarty->assign('connecter',1); 
		$smarty->assign('alias',$_SESSION["joueur"]->reqAlias());
		$smarty->assign('motDePasse',$_SESSION["joueur"]->reqMotDePasse());
		$smarty->assign('acces',$_SESSION["joueur"]->reqCategorie());
	}
	else
	{
		redirection('index.php',0);
		return;
	}
	
	$smarty->assign('titre',$lang['titre_admin']);
    $smarty->cache_lifetime = 0;
    $smarty->display('header.tpl');
    if(isset($_SESSION['css']))
    {
    	$smarty->assign('css',$_SESSION['css']);
    }
	 
	$smarty->cache_lifetime = 0;
	$smarty->display('menu.tpl');
    
    if(!isset($_GET["action"]))
    {
		formNouvelle();
	}
	else
    {
        $action = $_GET["action"];
        switch($action)
        {
            case "ajoutNouvelle":
                formAjoutNouvelle("");
                break;
            case "insertNouvelle":
                insertNouvelle($_POST["titre"],$_POST["nouvelle"],$_POST["destinataire"],
                  $_POST['image'],$_POST['langue']);
                break;
            case "updateNouvelle":
                formModifierNouvelle($_GET["cleNouvelle"],"");
                break;
            case "doUpdateNouvelle":
                updateNouvelle($_GET["cleNouvelle"],$_POST["titre"],
                    $_POST["nouvelle"],$_POST["destinataire"],$_POST['image'],$_POST['langue']);
                break;
            case "deleteNouvelle":
                supprimerNouvelle($_GET["cleNouvelle"]);
                break;
        }
    }

    $smarty->cache_lifetime = -1;
    $smarty->display("footer.tpl");
  }
  catch(SQLException $e)
  {
    echo $e->exception_dump();
  }
  catch(MyException $e)
  {
    echo $e->exception_dump();
  }

}

/*******************************************************************************
Fonction : updateNouvelle($cle,$titre,$nouvelle)
Param�tre :
    - $cle : la cl� unique de la nouvelle
    - $titre : le titre de la nouvelle
    - $nouvelle : la nouvelle
Description : met � jour la nouvelle
*******************************************************************************/
function updateNouvelle($cle,$titre,$texte,$destinataire,$image,$cleLangue)
{
  global $lang;
  if($titre=="")
    formModifierNouvelle($cle,$lang['nouvelle_titre_vide']);
  elseif($texte=="")
    formModifierNouvelle($cle,$lang['nouvelle_nouvelle_vide']);
  else
  {
      $nouvelle=new UneNouvelle($_SESSION["mysqli"]);
      $nouvelle->chargerMySQL($cle);
      $nouvelle->asgTitre(addslashes($titre));
      $nouvelle->asgNouvelle(addslashes(wordwrap($texte,50,"\r\n")));
      $nouvelle->asgImage($image);
      $nouvelle->asgDestinataire($destinataire);
      $nouvelle->asgCleLangue($cleLangue);
      $nouvelle->miseAJourMySQL();
      formNouvelle();
  }

}

/*******************************************************************************
Fonction : supprimerNouvelle($cle)
Param�tre :
    - $cle : la cl� de la nouvelle � supprimer
Description : supprime la nouvelle correspondant � la cl� pass� en param�tre
*******************************************************************************/
function supprimerNouvelle($cle)
{
  if($cle>=0)
  {
    $nouvelle=new UneNouvelle($_SESSION["mysqli"]);
    $nouvelle->chargerMySQL($cle);
    $nouvelle->deleteMySQL();
  }
  formNouvelle();
}

/*******************************************************************************
Fonction : insertNouvelle($titre,$nouvelle)
Param�tre :
    - $titre : le titre de la nouvelle
    - $nouvelle : la nouvelle
Description : ajoute une nouvelle � la table
*******************************************************************************/
function insertNouvelle($titre,$texte,$destinataire,$image,$cleLangue)
{
  global $lang;
  if($titre=="")
  {
    formAjoutNouvelle($lang['nouvelle_titre_vide']);
  }
  elseif($texte=="")
  {
    formAjoutNouvelle($lang['nouvelle_nouvelle_vide']);
  }
  else
  {
    $nouvelle=new UneNouvelle($_SESSION["mysqli"]);
    $nouvelle->asgTitre(addslashes($titre));
    $nouvelle->asgNouvelle(addslashes(wordwrap($texte,50,"\r\n")));
    $nouvelle->asgImage($image);
    $nouvelle->asgDestinataire($destinataire);
    $nouvelle->asgCleLangue($cleLangue);
    $nouvelle->insertionMySQL();
    formNouvelle();
  }
}

/*******************************************************************************
Fonction : formNouvelle()
Param�tre :
Description : affiche les nouvelles avec des liens vers la modification
    et la suppression
*******************************************************************************/
function formNouvelle()
{
  	global $lang;
  	$smarty = new MonSmarty($_SESSION['langage']);
  
    $nouvelles = new Nouvelles($_SESSION["mysqli"]);
    //on charge toutes les nouvelles
    $nouvelles->chargerTouteMySQL();
    for($i=0;$i<$nouvelles->reqNbNouvelle();$i++)
    {
        $nouvelle=$nouvelles->reqNouvelle($i+1);
        $arr[$i]['cle'] = $nouvelle->reqCle();
        $arr[$i]['date'] = convertirDateEnString($nouvelle->reqDate());
        $arr[$i]['titre'] = $nouvelle->reqTitre();
        $arr[$i]['nouvelle'] = htmlentities(substr($nouvelle->reqNouvelle(),0,120));
        if(strlen($nouvelle->reqNouvelle())>120)
        {
        	$arr[$i]['nouvelle'] .= " ...";
        }
        $arr[$i]['destinataire'] = $lang['type_destinataire'][$nouvelle->reqDestinataire()];
    }
    
    if($nouvelles->reqNbNouvelle() > 0)
    {
    	$smarty->assign('nouvelles',$arr);
    }
    	
    $smarty->cache_lifetime = 0;
    $smarty->display('liste_nouvelle.tpl');
}


/*******************************************************************************
Fonction : formModifierNouvelle($cle)
Param�tre :
    $cle : le num�ro de la nouvelle � modifier
    $erreur : un message d'erreur s'il y en a
Description : afficher le formulaire pour la modification de la nouvelle
    qui correspond � $cle
*******************************************************************************/
function formModifierNouvelle($cle,$erreur)
{
  $smarty = new MonSmarty($_SESSION['langage']);
  $smarty->assign('erreur',$erreur);
  $smarty->assign('action','modifier');

  $nouvelle = new UneNouvelle($_SESSION["mysqli"]);
  if(!$nouvelle->chargerMySQL($cle))
    return;
  
  $smarty->assign('cle', $nouvelle->reqCle());
  $smarty->assign('titre',$nouvelle->reqTitre());
  $smarty->assign('nouvelle',$nouvelle->reqNouvelle());
  $smarty->assign('dateLongue',convertirDateEnString($nouvelle->reqDate()));
  $smarty->assign('selected' . $nouvelle->reqDestinataire(),'selected');
  $smarty->assign('langue',$nouvelle->reqCleLangue());

  imageDir($smarty,$nouvelle->reqImage());
  $smarty->cache_lifetime = 0;
  $smarty->display('ajout_mod_nouvelle.tpl');

}

/*******************************************************************************
Fonction : imageDir
Param�tre :
    - $smarty : l'adresse de l'objet smarty
    - $img : l'image � afficher(modification de nouvelle)
            ou bien vide si on ajoute une nouvelle
Description : parcourir le dossier des images reli� au nouvelle et
    assigner ces informations � smarty
*******************************************************************************/
function imageDir(&$smarty,$img)
{
  $dir = IMAGE_DIR;
  $i=0;

  if($img!="")
  {
    $smarty->assign('image',$img);
  }

  $arr[' '] = " ";
  //on parcour le dossier qui contient les images des nouvelles
  if (is_dir($dir))
  {
   if ($dh = opendir($dir))
   {
       while (($file = readdir($dh)) !== false)
       {
         if(!is_dir($file))
         {
            $arr['img/sujet/' . $file] = $file;
            if($i==0 && $img=="")
                $smarty->assign('image','img/sujet/' . $file);
            $i++;
         }

       }
       closedir($dh);
   }
  }
  $smarty->assign('img',$arr);

}

/*******************************************************************************
Fonction : formAjoutNouvelle()
Param�tre : $erreur : un message d'erreur s'il y en a
Description : afficher le formulaire pour l'ajout d'une nouvelle
*******************************************************************************/
function formAjoutNouvelle($erreur)
{
  $smarty = new MonSmarty($_SESSION['langage']);
  $smarty->assign('erreur',$erreur);
  $smarty->assign('action','ajout');
  if(isset($_POST["titre"]))
  {
    $smarty->assign('titre',$_POST["titre"]);
    $smarty->assign('nouvelle',$_POST["nouvelle"]);
    $smarty->assign('selected' . $_POST['destinataire'],"selected");
  }
  $smarty->assign('dateLongue',convertirDateEnString(date("Y-m-d")));

  imageDir($smarty,"");
  
  $smarty->cache_lifetime = 0;
  $smarty->display('ajout_mod_nouvelle.tpl');

}

?>