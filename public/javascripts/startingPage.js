$(function () {
	// Notes variables
	let $create = $("#registrationButton");
	let $createForm = $("#registrationForm");
	let $signIn = $("#signInButton");
	let $signInForm = $("#signInForm");

	$create.click(function () {
		$signInForm.hide();
		$createForm.show();
	});
	$signIn.click(function () {
		$createForm.hide();
		$signInForm.show();
	});
});