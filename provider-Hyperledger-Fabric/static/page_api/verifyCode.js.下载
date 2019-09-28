function changeImg() {
	var imgSrc = $("#imgObj");
	var src = imgSrc.attr("src");
	imgSrc.attr("src", chgUrl(src));
}
// 时间戳
// 为了使每次生成图片不一致，即不让浏览器读缓存，所以需要加上时间戳
function chgUrl(url) {
	var timestamp = (new Date()).valueOf();
	url = url.split("?").shift();
	if ((url.indexOf("&") >= 0)) {
		url = url + "×tamp=" + timestamp;
	} else {
		url = url + "?timestamp=" + timestamp;
	}
	return url;
}

function isRightCode() {
	var code = $("#veryCode").attr("value");
	code = "veryCode=" + code;
	alert(code);
	$.ajax({
		type : "POST",
		url : "resultServlet",
		data : code,
		success : callback
	});
}

function callback(data) {
	$("#info").html(data);
}