$("#form-add").validate({

	submitHandler:function(form){
		add();
	}
});

/**
 *
 */
function add() {
	var dataFormJson=$("#form-add").serialize();
	$.ajax({
		cache : true,
		type : "POST",
		url : "/changeLog/add",
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

