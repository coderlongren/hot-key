$("#form-edit").validate({

	submitHandler : function(form) {
		edit();
	}
});

function edit() {
	let dataFormJson = $("#form-edit").serialize();
	$.ajax({
		cache : true,
		type : "POST",
		url : "/rule/edit",
		data : dataFormJson,
		headers: {
			"Authorization":getCookie("token")
		},
		async : false,
		error : function(response,status,xhr) {

			$.modal.alertError("系统错误"+response);
		},
		fail : function(response,status,xhr) {

			$.modal.alertError("系统错误"+response);
		},
		success : function(response,status,xhr,data) {

			$.operate.saveSuccess(data);
		}
	});
}
