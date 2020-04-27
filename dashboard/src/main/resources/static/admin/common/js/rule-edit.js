$("#form-edit").validate({

	submitHandler : function(form) {
		edit();
	}
});

function edit() {
	var oldRuleStr = $("#oldRule").val();
	var dataFormJson = $("#form-edit").serialize();
	if(oldRuleStr == dataFormJson){ return false; }

	$.ajax({
		cache : true,
		type : "POST",
		url : "/rule/edit",
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
