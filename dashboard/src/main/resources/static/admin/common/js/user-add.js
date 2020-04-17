$("#form-add").validate({
	rules:{
		username:{
			required:true,
			minlength: 2,
			maxlength: 20,
			remote: {
                url: "/user/checkLoginNameUnique",
                type: "post",
                dataType: "json",
                dataFilter: function(data, type) {
                    if (data == "0")
                    	return true;
                    else 
                    	return false;
                }
            }
		},
		appName:{
			required:true,
		},
		pwd:{
			required:true,
			minlength: 5,
			maxlength: 20
		},
		email:{
			required:true,
            email:true,
            remote: {
                url:rootPath + "/UserController/checkEmailUnique",
                type: "post",
                dataType: "json",
                data: {
                    name: function () {
                        return $.trim($("#email").val());
                    }
                },
                dataFilter: function (data, type) {
                    if (data == "0") return true;
                    else return false;
                }
            }
		},
		phonenumber:{
			required:true,
			isPhone:true,
            remote: {
                url: rootPath + "/system/user/checkPhoneUnique",
                type: "post",
                dataType: "json",
                data: {
                    name: function () {
                        return $.trim($("#phonenumber").val());
                    }
                },
                dataFilter: function (data, type) {
                    if (data == "0") return true;
                    else return false;
                }
            }
		},
	},
	messages: {
        "userName": {
            remote: "用户已经存在"
        }
    },
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
		url : "/user/add",
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

