var timer = null;
var logTimer = null;
var $this = null;
$(function() {
	
	logout();
	userStatus();
	
	var browser={
	    versions:function(){
	        var u = navigator.userAgent, app = navigator.appVersion;
	        return {
	            trident: u.indexOf('Trident') > -1, //IE内核
	            presto: u.indexOf('Presto') > -1, //opera内核
	            webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
	            gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,//火狐内核
	            mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
	            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
	            android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
	            iPhone: u.indexOf('iPhone') > -1 , //是否为iPhone或者QQHD浏览器
	            iPad: u.indexOf('iPad') > -1, //是否iPad
	            webApp: u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
	        };
		}(),
		language:(navigator.browserLanguage || navigator.language).toLowerCase()
	};
	if(browser.versions.mobile || browser.versions.ios || browser.versions.android || browser.versions.iPhone || browser.versions.iPad){
	    $('body').addClass('wap');
	}
	
	//顶栏下拉框出现与延迟消失
	$(".nav-item-list").hover(
		function(){
			$(".sub-menu").show();
			$(".sub-menu").animate({"opacity": 0, "top": "60px"},100);
			clearTimeout(timer);
			$(this).parent().find(".sub-menu").animate({"opacity": 1, "top": "62px"},300);
		},
		function(){
			$this = $(this);
			timer = setTimeout(function() {
				$this.parent().find(".sub-menu").animate({"opacity": 0, "top": "60px"},200);
				$(".sub-menu").hide();
			}, 500);
		}
	);
	
	$(".sub-menu").hover(
		function(){
			clearTimeout(timer);
			$(".sub-menu").show();
		},
		function(){
			$(this).animate({"opacity": 0, "top": "60px"},200);
			$(".sub-menu").hide();
		}
	);
	
	$(".user-id").hover(
		function(){
			$(".user-info-menu-wrap").show();
			$(".user-info-menu-wrap").animate({"opacity": 0, "top": "60px"},100);
			clearTimeout(logTimer);
			$(this).parent().find(".user-info-menu-wrap").animate({"opacity": 1, "top": "62px"},300);
		},
		function(){
			$this = $(this);
			logTimer = setTimeout(function() {
				$this.parent().find(".user-info-menu-wrap").animate({"opacity": 0, "top": "60px"},200);
				$(".user-info-menu-wrap").hide();
			}, 500);
		}
	);
	
	$(".user-info-menu-wrap").hover(
		function(){
			clearTimeout(logTimer);
			$(".user-info-menu-wrap").show();
		},
		function(){
			$(this).animate({"opacity": 0, "top": "60px"},200);
			$(".user-info-menu-wrap").hide();
		}
	);
	//导航按钮hover的时候，滑块动画
	$(".nav-item").hover(
		function() {$(".nav-line").width($(this).width()); $(".nav-line").css("left", positionLeft($(this).parent(), $(this).parent().index()));},
		function() {$(".nav-line").width("0px")}
	);
	
	//开发者文档 hover - 滑块动画
	$(".sub-nav .item").hover(
		function() {
			$(".sub-nav-line").width($(this).width());
			$(".sub-nav-line").css("left", subNavItemPositionLeft($(this).parent(), $(this).index()));
		},
		function() {
			$(".sub-nav-line").width("0px")
		}
	);
	
	//登录按钮hover的时候，改变分割线的样式
	$(".login-btn").hover(
		function() {
			if($("body").find(".bm-header-wrap").hasClass("index-default")) {
				$(".nav-separation").css("background", "transparent");
			} else {
				$(".nav-separation").css("background", "#f7f8f8")
			}
		},
		function() {
			if($("body").find(".bm-header-wrap").hasClass("index-default")) {
				$(".nav-separation").css("background", "#fff");
			} else {
				$(".nav-separation").css("background", "#1b1b1b");
			}
		}
	);
	
	
});

//退出
var logout = function(){
	$(".baseInfo-NavOut").on("click", function() {
		$.ajax({
			type:"get",
			url:config.bumengDev + "user/logout",
			dataType:"json",
			success: function(data) {
				if(data.err_code == "0") {
					//清除cookie
					setCookie('bmLoginStatus', false, 0);
					$(".login-group").show();
					$(".logined-group").hide();
					setTimeout(function(){
						window.location = window.location.href;
					},1200);
					$("body").append(response.data.ucsysnlogin);
				} else {
					window.location = window.location.href;
				}
			}
		});
	});
}

//设置cookie
var setCookie = function(key, value, expiredays){
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + expiredays);
	document.cookie = key + "=" + escape(value) + ((expiredays==null) ? "" : ";expires=" + exdate.toGMTString()) + "; path=/; domain=bumeng.cn";
}

// 获得cookie
function getCookie(key){
	var strStart = strEnd = 0;
	
	if(document.cookie.length>0){
		var strStart = document.cookie.indexOf(key + "=");
		if (strStart != -1)
		{ 
			strStart = strStart + key.length + 1;
			strEnd = document.cookie.indexOf(";", strStart);
			if (strEnd==-1) strEnd = document.cookie.length;
			return unescape(document.cookie.substring(strStart, strEnd));
		} 
	}
	return "";
}

//用户状态
var userStatus = function(){
	if(getCookie('bmLoginStatus')){
		$(".login-group").hide();
		$(".logined-group").show();
	}else{
		$(".login-group").show();
		$(".logined-group").hide();
		//清除session
//		$.ajax({
//			type:"get",
//			url:config.bumengDev + "user/logout",
//			dataType:"json",
//			success: function(data) {
//				if(data.err_code == "0") {
//					$(".login-group").show();
//					$(".logined-group").hide();
//				}
//			}
//		});
	}
}
	
var positionLeft = function(parent_object, index) {
	var leftVal = 0;
	var midVal = 0;
	for(var i = 0; i < index; i++) {
		midVal = parseInt(parent_object.parent().find(".nav-group").eq(i).width()) + parseInt(parent_object.parent().find(".nav-group").eq(i).css("margin-right").split('px')[0]);
		leftVal += midVal;
	}
	return leftVal;
};

var subNavItemPositionLeft = function(parent_object, index) {
	var leftVal = 0;
	var midVal = 0;
	for(var i = 0; i < index; i++) {
		midVal = parseInt(parent_object.find(".item").eq(i).width()) + parseInt(parent_object.find(".item").eq(i).css("margin-right").split('px')[0]);
		leftVal += midVal;
	}
	return leftVal;
};