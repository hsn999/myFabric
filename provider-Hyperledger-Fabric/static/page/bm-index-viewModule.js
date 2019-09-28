var flag = false;
var flag1 = false;
var flag2 = true;
var flag3 = false;
var part1Timer, part2Timer, part3Timer, part4Timer;
var part01Timer, part02Timer, part03Timer, part04Timer;
$(function() {
	//移动端宽度兼容
//	var screen_width = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
//	$("body").width(screen_width);
	
	
	
//	//------登入
//	$.ajax({
//		type:"get",
//		url:config.bumengDev + "user/userInfo",
//		dataType:"json",
//		success:function(data) {
//			if(data.err_code == "0") {
//				$(".login-group").hide();
//				$(".logined-group").show();
//			} else if(data.err_code == "10003") {
//				$(".login-group").show();
//				$(".logined-group").hide();
//			}
//		}
//	});
//	//------登出
//	$(".baseInfo-NavOut").on("click", function() {
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
//	});
	//导航栏动画
	var animate_flag = true;
	var assetAnimate_flag = true;
	var userAnimate_flag = true;
	var businessAnimate_flag = true;
	$(window).off('scroll').on('scroll', function() {
		if($(document).scrollTop() > 60) {
			$(".bm-logo").attr("src", "images/nav-logo.png");
			$(".bm-header-wrap").removeClass("index-default").addClass("index-animate").find(".nav-separation").css("background", "#1b1b1b");
			if(animate_flag) {
				$(".bm-header-wrap").animate({"top":"0px"}, 100, function() {
					animate_flag = false;
				});
			}
			if($(document).scrollTop() > 1228 && $(document).scrollTop() < 1944) {
				if(assetAnimate_flag) {
					assetAnimate_flag = false;
					var asset_timer = setTimeout(function() {
						$(".asset").show();
						$(".asset").stop().animate({"left":"339px"}, 2000, function() {
							$(".asset").hide();
							$(".accountBook-before").stop().animate({"opacity": 0}, 300, function() {
								$(".accountBook-done").stop().animate({"opacity": 1}, 300, function() {
									setTimeout(function(){
										$(".accountBook-done").stop().animate({"opacity": 0}, 300, function() {
											//$(".accountBook-before").stop().animate({"opacity": 1}, 300, function() {
//												$(".asset").css({"left":"187px"});
//												$(".asset").show();
//												$(".asset").stop().animate({"left":"263px"}, 2000);
												$(".accountBook-before").css({"opacity": 1});
												$(".asset").show().css({"left":"263px"});
											//});
										});
									}, 1000);
								});
							});
						});
					}, 0);
				}
			} else if($(document).scrollTop() > 2402 && $(document).scrollTop() < 3180) {
				if(userAnimate_flag) {
					userAnimate_flag = false;
					$(".bm-user-diamond").stop().animate({"opacity": 1}, 300, function() {
						$(".bm-user-diamond").stop().animate({"top": "130px"}, 1000);
					});
					$(".bm-user-talk").stop().animate({"opacity": 1}, 300, function() {
						$(".bm-user-talk").stop().animate({"top": "2px"}, 1000);
					});
					$(".bm-user-gold").stop().animate({"opacity": 1}, 300, function() {
						$(".bm-user-gold").stop().animate({"top": "-45px"}, 1000);
					});
					$(".bm-user-percent").stop().animate({"opacity": 1}, 300, function() {
						$(".bm-user-percent").stop().animate({"top": "0px"}, 1000);
					});
					$(".bm-user-connect").stop().animate({"opacity": 1}, 300, function() {
						$(".bm-user-connect").stop().animate({"top": "60px"}, 1000);
					});
					$(".bm-user-up").stop().animate({"opacity": 1}, 300, function() {
						$(".bm-user-up").stop().animate({"top": "150px"}, 1000);
					});
				}	
			} else if($(document).scrollTop() > 3179 && $(document).scrollTop() < 3854) {
				if(businessAnimate_flag) {
					businessAnimate_flag = false;
					animateBusiness($(".business-animate-gold"), "117px", "118px", 1000, "204px", "335px");
					animateBusiness($(".business-animate-dollar"), "80px", "414px", 1000, "80px", "115px");
				}
			}
		} else {
			animate_flag = true;
			$(".bm-logo").attr("src", "images/nav-logo-old.png");
			$(".bm-header-wrap").removeAttr("style").removeClass("index-animate").addClass("index-default").find(".nav-separation").css("background", "#fff");
		}
		
		if($(".slogan-wrap").offset().top - $(document).scrollTop() < 310) {
			if($(".slogan-wrap").offset().top - $(document).scrollTop() > 0) {
				sloganTimer();
			}
		}
	});
	
//	$(".slogan-wrap").hover(
//		function() {
//			flag = false; 
//			flag1 = false; 
//			flag2 = false; 
//			flag3 = false;
//			clearInterval(part1Timer);
//			clearInterval(part2Timer);
//			clearInterval(part3Timer);
//			clearInterval(part4Timer);
//			clearTimeout(part01Timer);
//			clearTimeout(part02Timer);
//			clearTimeout(part03Timer);
//			clearTimeout(part04Timer);
//			$(".slogan-item-second").hide().removeClass("fadeInUp").addClass("fadeOutDown");
//			$(".slogan-item-first").show().removeClass("fadeOutDown").addClass("fadeInUp");
//		},
//		function() {
//			$(".slogan-item-second").removeClass("fadeOutDown");
//			$(".slogan-item-first").removeClass("fadeInUp");
//			flag2 = true;
//			sloganTimer();
//		}
//	);
	//轮播器
	var banner_swiper = new Swiper('.swiper-container', {
		direction: 'horizontal',
		loop: false,
		autoplay: 5000,
		speed: 1000,
		
		autoplayDisableOnInteraction : false,
		
		pagination: '.swiper-pagination',
		paginationClickable :true,
		
		keyboardControl : true,
	});
	//布萌服务
	var serviceTurn = function(target,time,opts){

		target.find('.bm-service-animate-item').hover(function(){
	
			$(this).find('.zheng').stop().animate(opts[0],time,function(){
	
				$(this).hide().next().show();
	
				$(this).next().animate(opts[1],time);
	
			});
	
		},function(){
	
			$(this).find('.fan').animate(opts[0],time,function(){
	
				$(this).hide().prev().show();
	
				$(this).prev().animate(opts[1],100);
	
			});
	
		});
	
	}
	var verticalOpts = [{'width':0},{'width':'274px'}];
	var target_animate = $(".bm-service-animate");
	if(serviceTurn) {
		serviceTurn(target_animate, 200, verticalOpts);
	}
	//serviceTurn(target_animate, 200, verticalOpts);
	//资产实例
	var asset_swiper = new Swiper('.asset-example-swiper', {
		speed: 1000,
    		
    		grabCursor : false,
    		
    		preventClicks : false,
    		loop : false,
		slidesPerView : 4,
//		loopedSlides :4,
		
		prevButton:'.swiper-button-prev',
		nextButton:'.swiper-button-next',
	});
	//导航数据栏
	$(".item-img").hover(
		function(){$(this).attr("src", $(this).attr("src").split(".png")[0] + "-hover.png")},
		function(){$(this).attr("src", $(this).attr("src").split("-hover.png")[0] + ".png")}
	);
});

var sloganTimer = function() {
	part1Timer = setInterval(function() {
		if(flag2) {
			flag2 = false;
			part01Timer = setTimeout(function(){
				$(".slogan-item-second").hide().removeClass("fadeOutDown");
				$(".slogan-item-first").addClass("fadeOutDown");
				flag3 = true;
			}, 5000);
		}
	}, 100);
	part2Timer = setInterval(function(){
		if(flag3) {
			flag3 = false;
			part02Timer = setTimeout(function(){
				$(".slogan-item-first").hide().removeClass("fadeOutDown");
				$(".slogan-item-second").show().addClass("fadeInUp");
				flag = true;
			}, 100);
		}
	}, 100);
	part3Timer = setInterval(function(){
		if(flag) {
			flag = false;
			part03Timer = setTimeout(function(){
				$(".slogan-item-first").hide();
				$(".slogan-item-second").show().removeClass("fadeInUp").addClass("fadeOutDown");
				flag1 = true;
			}, 5000);
		}
	}, 100);
	part4Timer = setInterval(function(){
		if(flag1) {
			flag1 = false;
			part04Timer = setTimeout(function(){
				$(".slogan-item-second").hide().removeClass("fadeOutDown");
				$(".slogan-item-first").show().addClass("fadeInUp");
				flag2 = true;
			}, 100);
		}
	}, 100);
}
//布萌服务翻转
//var serviceTurn = function(target,time,opts){
//
//	target.find('.bm-service-animate-item').hover(function(){
//
//		$(this).find('.zheng').stop().animate(opts[0],time,function(){
//
//			$(this).hide().next().show();
//
//			$(this).next().animate(opts[1],time);
//
//		});
//
//	},function(){
//
//		$(this).find('.fan').animate(opts[0],time,function(){
//
//			$(this).hide().prev().show();
//
//			$(this).prev().animate(opts[1],time);
//
//		});
//
//	});
//
//}

//商业机会动画
var animateBusiness = function(obj, end_top, end_left, time, start_top, start_left) {
	obj.stop().animate({"opacity": 1}, 1000, function() {
		obj.stop().animate({"top": end_top, "left": end_left}, time, function() {
			obj.stop().animate({"opacity": 0}, 300, function() {
				var businessAnimate_timer = setTimeout(function() {
					obj.css({"top": start_top, "left": start_left});
					animateBusiness(obj, end_top, end_left, time, start_top, start_left);
					clearTimeout(businessAnimate_timer);
				}, 1000);
			});	
		});	
	});
}