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
			<div class=""><h3>Create Bundle</h3></div>

			<h4 id="demo-zero-configuration">Name <input type="text" value="Unified Desktop"></input></h4>

			<div class="row-fluid">
				<div class="span5">
					<select name="from" id="multiselect" class="span12" size="8" multiple="multiple">
<option value="1">Atom_Framework</option>
<option value="2">Atom_Profile</option>
<option value="3">Atom_Samples</option>
<option value="4">Atom_UI_Resources</option>
<option value="5">BRMS Batch Processor</option>
<option value="6">BRMS Bulk Trading Batch</option>
<option value="7">BRMS Document Confirmation Batch</option>
<option value="8">BRMS Document Confirmation Rules</option>
<option value="9">BRMS Enrollment Rules</option>
<option value="10">BRMS General Rules</option>
<option value="11">BRMS Lump Sum Cash Withdrawal Rules</option>
<option value="12">BRMS MDM Rules</option>
<option value="13">BRMS Mutual Funds Rules</option>
<option value="14">BRMS Ping Rule</option>
<option value="15">BRMS Portfolio Advice Rules</option>
<option value="16">BRMS Retirement Illustration Rules</option>
<option value="17">BRMS Retirement Plan Transaction Rules</option>
<option value="18">BRMS Rules</option>
<option value="19">BRMS WODM</option>
<option value="20">UnifiedDesktopMyBoB</option>
<option value="21">UnifiedDesktopBulkEmail</option>
<option value="22">UnifiedDesktopParticipant</option>
<option value="23">UnifiedDesktopEnrollment</option>
<option value="24">UnifiedDesktopFraudAnalysis</option>
<option value="25">UnifiedDesktopEnrollmentServices</option>
<option value="26">UnifiedDesktopParticipantServices</option>
<option value="27">UnifiedDesktopIRAEnrollmentEAR</option>
<option value="28">UnifiedDesktopInternalUserProfileRS</option>
<option value="29">UnifiedDesktopCore</option>
<option value="30">UnifiedDesktopTransactionCoreServices</option>
<option value="31">UnifiedDesktopContent</option>
<option value="32">UnifiedDesktopBrokerageEnrollment</option>
<option value="33">UnifiedDesktopAssociate</option>
					</select>
				</div>

				<div class="span2">
					<button type="button" id="multiselect_rightAll" class="btn btn-block"><i class="icon-forward"></i></button>
					<button type="button" id="multiselect_rightSelected" class="btn btn-block"><i class="icon-chevron-right"></i></button>
					<button type="button" id="multiselect_leftSelected" class="btn btn-block"><i class="icon-chevron-left"></i></button>
					<button type="button" id="multiselect_leftAll" class="btn btn-block"><i class="icon-backward"></i></button>
				</div>

				<div class="span5">
					<select name="to" id="multiselect_to" class="span12" size="8" multiple="multiple"></select>
				</div>
			</div>

			<div class="span5">
				<button type="submit">Save</i></button>
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