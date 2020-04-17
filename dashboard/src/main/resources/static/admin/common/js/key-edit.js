$("#form-edit").validate({

	submitHandler : function(form) {
		edit();
	}
});

function edit() {
	var dataFormJson = $("#form-edit").serialize();
	$.ajax({
		cache : true,
		type : "POST",
		url : "/key/edit",
		data : dataFormJson,
		headers: {
			"Authorization":getCookie("token")
		},
		async : false,
		error : function(request) {
			$.modal.alertError("系统错误");
		},
		success : function(data) {
			$.operate.saveSuccess(data);
		}
	});
}
