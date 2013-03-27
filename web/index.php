<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>CraftZ</title>
		<meta name="author" content="JangoBrick" />
		<meta name="copyright" content="JangoBrick" />
		<meta name="keywords" content="minecraft, bukkit, craftz, server" />
		<meta name="description" content="" />
		<meta name="ROBOTS" content="INDEX, NOFOLLOW" />
		<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
		<meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
		<meta http-equiv="content-style-type" content="text/css" />
		<link rel="stylesheet" href="/data/css/style.css" />
	</head>
	<body>
			
		<div id="logobox"><img src="/data/img/logo_hd.jpg" alt="CraftZ" /></div>
		
		<div id="navi">
				
				<span class="navi_button">
					<a href="/">Home</a>
				</span>				
							
			</div>		
		
		<div id="content">
			
			<?php
				
				$ipage = 'content/start.php';
				
				if (isset($_GET['page']))	{
					
				    if ($_GET['page'] == disclaimer) {
					    	
					    $ipage = 'content/default.php';
					    readfile('http://www.disclaimer.de/disclaimer.htm');
					    
							} else {
					        
					        $page = basename($_GET['page']);
					        
					        $tpage = 'content/' . $page . '.php';
					        
					        if (file_exists($tpage)) {
					            $ipage = $tpage;
					        } else {
					         $ipage = 'content/error404.php'; /* Wenn nicht, wird eine Fehlermeldung eingebunden */
					        }
					        
				    	}
				    
				}
				
				include($ipage);
				
			?>
		</div>
		
	</body>
</html>