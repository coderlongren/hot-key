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
			alert("response: "+response);	//服务器返回的信息
			alert("status: "+status);	//服务器返回的信息
			alert("xhr.status: "+xhr.status);	//状态码,   要看其他的直接 输出 xhr 就行
			alert("Headers: "+xhr.getAllResponseHeaders()); //响应头部
			$.modal.alertError("系统错误111111"+response);
		},
		fail : function(response,status,xhr) {
			alert("failresponse: "+response);	//服务器返回的信息
			alert("failstatus: "+status);	//服务器返回的信息
			alert("failxhr.status: "+xhr.status);	//状态码,   要看其他的直接 输出 xhr 就行
			alert("failHeaders: "+xhr.getAllResponseHeaders()); //响应头部
			$.modal.alertError("系统错误"+response);
		},
		success : function(response,status,xhr,data) {
			alert(response);	//服务器返回的信息
			alert(status);	//服务器返回的信息
			alert(xhr.status);	//状态码,   要看其他的直接 输出 xhr 就行
			alert(xhr.getAllResponseHeaders()); //响应头部
			$.operate.saveSuccess(data);
		}
	});
}
