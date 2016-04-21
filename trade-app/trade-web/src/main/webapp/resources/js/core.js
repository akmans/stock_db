
function createForm() {
	var div = document.getElementById('market-form');
	var form = document.createElement('form');
	form.setAttribute('action', '/markets');
	form.setAttribute('method', 'POST');

	/*----code---*/
	var code = document.createElement('div');
	code.appendChild(document.createTextNode('-'));
	code.setAttribute('class', 'col-xs-2 col-md-1');
	/*-----------*/

	/*----name---*/
	var input_name = document.createElement('input');
	input_name.setAttribute('type', 'text');
	input_name.setAttribute('placeholder', 'Name');
	input_name.setAttribute('name', 'name');
	input_name.setAttribute('id', 'name');
	input_name.setAttribute('class', 'form-control');
	var name = document.createElement('div');
	name.appendChild(input_name);
	name.setAttribute('class', 'col-xs-10 col-md-9');
	/*-----------*/

	/*--clearfix-*/
	var clearfix = document.createElement('div');
	clearfix.setAttribute('class', 'clearfix visible-xs-block');
	/*-----------*/

	/*---button--*/
	var input_button = document.createElement('input');
	input_button.setAttribute('type', 'submit');
	input_button.setAttribute('value', '実行');
	input_button.setAttribute('name', 'commit');
	input_button.setAttribute('class', 'btn btn-default');
	var button = document.createElement('div');
	button.appendChild(input_button);
	button.setAttribute('class', 'col-xs-8 col-md-2 text-right');

	form.appendChild(code);
	form.appendChild(name);
	form.appendChild(clearfix);
	form.appendChild(button);

	div.appendChild(form);	
}
