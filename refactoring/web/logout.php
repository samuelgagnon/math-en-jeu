<?php
/*******************************************************************************
Fichier : logout.php
Auteur : Maxime B�gin
Description : D�connecte un joueur
********************************************************************************
26-11-2006 Maxime B�gin - Version initiale
*******************************************************************************/

require_once("lib/ini.php");

unset($_SESSION["joueur"]);
redirection("index.php",0);

?>