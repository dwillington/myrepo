<!DOCTYPE html>
<html lang="en">
<head>
	<meta name="description" lang="en" content="jQuery multiselect plugin with two sides. The user can select one or more items and send them to the other side."/>
	<meta name="keywords" lang="en" content="jQuery multiselect plugin" />

	<title>jQuery multiselect plugin with two sides</title>

    <script src="js/multiselect.js"></script>

	<link rel="icon" type="image/x-icon" href="https://github.com/favicon.ico" />
	<link rel="stylesheet" href="lib/bootstrap/css/bootstrap.min.css" />
	<link rel="stylesheet" href="lib/google-code-prettify/prettify.css" />
	<link rel="stylesheet" href="css/style.css" />
</head>
<body>

	<div id="wrap">
		<div id="home" class="container">
			<div class="hero-unit">
				<h1>Sonar Over Time</h1>
			</div>
		</div>

		<div id="demo" class="container">
			<div class=""><h3>View Bundles</h3></div>

			<h4 id="demo-zero-configuration">Atom</h4>

			<div class="row-fluid">
				<div class="span5">
					<select name="from" id="multiselect" class="span12" size="8" multiple="multiple">
<option value="1">Atom_Framework</option>
<option value="2">Atom_Profile</option>
					</select>
				</div>
			</div>

			<h4 id="demo-zero-configuration">Unified Desktop</h4>

			<div class="row-fluid">
				<div class="span5">
					<select name="from" id="multiselect" class="span12" size="8" multiple="multiple">
<option value="1">UnifiedDesktopMyBoB</option>
<option value="2">UnifiedDesktopBulkEmail</option>
<option value="3">UnifiedDesktopParticipant</option>
<option value="4">UnifiedDesktopEnrollment</option>
					</select>
				</div>
			</div>


	</div>


	<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	<script type="text/javascript" src="lib/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="lib/google-code-prettify/prettify.js"></script>
	<script type="text/javascript" src="js/multiselect.js"></script>

	<script type="text/javascript">
	$(document).ready(function() {
		// make code pretty
		window.prettyPrint && prettyPrint();

		if ( window.location.hash ) {
			scrollTo(window.location.hash);
		}

		$('.nav').on('click', 'a', function(e) {
			scrollTo($(this).attr('href'));
		});

		$('#multiselect').multiselect();
		$('.multiselect').multiselect();
		$('.js-multiselect').multiselect({
			right: '#js_multiselect_to_1',
			rightAll: '#js_right_All_1',
			rightSelected: '#js_right_Selected_1',
			leftSelected: '#js_left_Selected_1',
			leftAll: '#js_left_All_1'
		});
	});

	function scrollTo( id ) {
		if ( $(id).length ) {
			$('html,body').animate({scrollTop: $(id).offset().top - 40},'slow');
		}
	}
	</script>

</body>
</html>