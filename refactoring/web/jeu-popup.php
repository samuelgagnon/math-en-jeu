<?php

require_once("lib/ini.php");

// check if there is a session for this player
if (!isset($_SESSION["joueur"])) {
  echo "<script>window.close()</script>";
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" style="margin:0px;padding:0px;height:100%;">
<head>

<meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT"/>
<meta http-equiv="Cache-Control" content="no-cache, max-age=0"/>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">

<script type="text/javascript">
<!-- Begin

function Launch(page) {
 OpenWin = window.open(page, "Aide", "toolbar=no,menubar=no,location=no,scrollbars=auto,resizable=yes,width=350,height=250");
}
// End -->
</script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Math en jeu</title>
</head>
<body bgcolor="#000000" style="margin:0px;padding:0px;height:100%;width:100%;">


<div style="height:100%">

<OBJECT 
	classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
	codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0"
	WIDTH="100%" HEIGHT="100%" id="mathenjeu">
	<PARAM NAME=movie VALUE="<?php echo FLASH_DIR ?>/chargement_beta.swf?nomUtilisateur=<?php echo $_SESSION["joueur"]->reqAlias(); ?>&amp;motDePasse=<?php echo $_SESSION["joueur"]->reqMotDePasse(); ?>&amp;langue=<?php echo $_SESSION['langage']; ?>&amp;gameType=mathEnJeu&amp;path=<?php echo FLASH_DIR ?>">
	<PARAM NAME=quality VALUE=high>
	<param name="bgcolor" value="#000000" />
	<param name="allowFullScreen" value="true" />
	<EMBED src="<?php echo FLASH_DIR ?>/chargement_beta.swf?nomUtilisateur=<?php echo $_SESSION["joueur"]->reqAlias(); ?>&amp;motDePasse=<?php echo $_SESSION["joueur"]->reqMotDePasse(); ?>&amp;langue=<?php echo $_SESSION['langage']; ?>&amp;gameType=mathEnJeu&amp;path=<?php echo FLASH_DIR ?>" 
		quality=high bgcolor=#000000 WIDTH="100%" HEIGHT="100%"
		NAME="mathenjeu" TYPE="application/x-shockwave-flash"
		PLUGINSPAGE="http://www.macromedia.com/go/getflashplayer">
	</EMBED>
</OBJECT>

</div>
</body>
</html>