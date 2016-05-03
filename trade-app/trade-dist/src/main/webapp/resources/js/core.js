$(document).ready(function() {
	// initialize.
	initialize();
});

/*
 * Ajax function to get data for update.
 */
function doAjaxGet(url) {
	// Do ajax process.
	$.ajax({
		type : "GET",
		url : url,
		dataType : "html",
		success : function(data, status, xhr) {
			// Render market-form.
			$('#dialog-entry-form').html(data);
			// Show entry form.
			showEntryForm();
		},
		error : function(XMLHttpRequest, status, errorThrown) {
			// Handle error.
		}
	});
}

/*
 * Show entry form.
 */
function showEntryForm() {
	// Show entry form dialog.
	$('#dialogModal').modal('show');

	// initialize event.
	initializeEntryForm();
}

/*
 * Close entry form.
 */
function closeEntryForm() {
	// Close entry form using slide up.
	$('#dialogModal').modal('hide');
	$('body').removeClass('modal-open');
	$('.modal-backdrop').remove();
}

/*
 * Initialize event in the entry form.
 */
function initializeEntryForm() {
	// Bind function to submit.
	$('#entry-form').submit(function(event) {
		// Prevent default action.
		event.preventDefault();
		var pageNum = $('#currentPage').val();
		var pageSize = $('#pageSize').val();
		// Do ajax process.
		$.ajax({
			type : "POST",
			url : $('#entry-form').attr('action') + "?page=" + pageNum + "&size=" + pageSize,
			data : $('#entry-form').serialize(),
			dataType : "html",
			success : function(data, status, xhr) {
				if (data.indexOf('id="entry-form"') !== -1) {
					// Render dialog-entry-form.
					$('#dialog-entry-form').html(data);
					// initialize event.
					initializeEntryForm();
				} else {
					// Close entry form.
					closeEntryForm();
					// Render container.
					$('#container').html(data);
					// initialize event.
					initialize();
				}
			},
			error : function(xhr, status, error) {
				var data = $(xhr.responseText);
				var messages = $('#container', data);
				$("#messages").html(messages).show();
			}
		})
	});

	// Bind function to click.
	$('a#confirm').bind("click", function(event) {
		// Prevent default action.
		event.preventDefault();
		// Submit entry form.
		$('#entry-form').submit();
	});
}

/*
 * Initialize event in the content.
 */
function initialize() {
	/*
	 * Bind event to add button.
	 */
	$("a#add").click(function(event) {
		// Prevent default action.
		event.preventDefault();
		var paramUrl = $(this).attr("href");
		doAjaxGet(paramUrl);
	});

	/*
	 * Bind event to edit button.
	 */
	$("a.edit").click(function(event) {
		// Prevent default action.
		event.preventDefault();
		var paramUrl = $(this).attr("href");
		doAjaxGet(paramUrl);
	});

	/*
	 * Bind event to delete button.
	 */
	$("a.delete").click(function(event) {
		// Prevent actual form submit and page reload
		event.preventDefault();
		var paramUrl = $(this).attr("href");
		doAjaxGet(paramUrl);
	});
}
